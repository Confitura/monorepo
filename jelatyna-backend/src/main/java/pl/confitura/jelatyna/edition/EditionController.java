package pl.confitura.jelatyna.edition;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.confitura.jelatyna.agenda.DayRepository;
import pl.confitura.jelatyna.infrastructure.published.WebpageDataDumper;
import pl.confitura.jelatyna.platform.security.JelatynaPrincipal;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.voting.VoteRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

/**
 * Lets admins manage Editions; every endpoint is admin-only. The backend needs exactly one Current Edition, so
 * only {@link #makeEditionCurrent} changes which one it is: a new Edition is never current, an update keeps
 * whether it is, and the current one is never deleted.
 */
@RestController
@RequestMapping("/editions")
@PreAuthorize("@security.isAdmin()")
@RequiredArgsConstructor
@Slf4j
public class EditionController {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

    private final EditionRepository repository;
    private final PresentationRepository presentationRepository;
    private final DayRepository dayRepository;
    private final VoteRepository voteRepository;
    private final TransactionTemplate transactionTemplate;
    private final WebpageDataDumper webpageDataDumper;

    /** Every Edition, newest year first. */
    @GetMapping
    public List<EditionDto> getAllEditions() {
        return repository.findAllByOrderByYearDesc().stream().map(EditionDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditionDto> getEdition(@PathVariable String id) {
        Edition edition = repository.findById(id);
        return edition == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(EditionDto.from(edition));
    }

    /** A new Edition is never current. */
    @PostMapping
    public ResponseEntity<EditionDto> createEdition(@RequestBody EditionRequest request) {
        if (request.year() == null || !isValid(request)) {
            return ResponseEntity.badRequest().build();
        }
        if (repository.existsByYear(request.year())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Edition edition = new Edition()
                .setYear(request.year())
                .setName(request.name())
                .setConferenceStart(request.conferenceStart())
                .setLastDay(request.lastDay())
                .setC4pStart(request.c4pStart())
                .setC4pEnd(request.c4pEnd());
        return ResponseEntity.status(HttpStatus.CREATED).body(EditionDto.from(repository.save(edition)));
    }

    /**
     * Replaces the name, conference dates and C4P period, so a null C4P date closes it. The year can't change,
     * and neither does whether the Edition is current.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EditionDto> updateEdition(@PathVariable String id, @RequestBody EditionRequest request) {
        Edition edition = repository.findById(id);
        if (edition == null) {
            return ResponseEntity.notFound().build();
        }
        if (!Objects.equals(request.year(), edition.getYear()) || !isValid(request)) {
            return ResponseEntity.badRequest().build();
        }
        edition.setName(request.name())
                .setConferenceStart(request.conferenceStart())
                .setLastDay(request.lastDay())
                .setC4pStart(request.c4pStart())
                .setC4pEnd(request.c4pEnd());
        return ResponseEntity.ok(EditionDto.from(repository.save(edition)));
    }

    /** Refused while the Edition is current or owns a talk, workshop or agenda Day. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEdition(@PathVariable String id) {
        Edition edition = repository.findById(id);
        if (edition == null) {
            return ResponseEntity.notFound().build();
        }
        if (edition.isCurrent() || presentationRepository.existsByEdition(edition) || dayRepository.existsByEdition(edition)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Makes the Edition current (ADR-0004). In one transaction the previous Current Edition's votes lose their
     * voters' IP addresses and it stops being current; then the dumps are written into the new Edition's folder.
     * Refused for the Edition that is already current, which would clear nothing and dump for no reason.
     */
    @PostMapping("/{id}/make-current")
    public ResponseEntity<EditionDto> makeEditionCurrent(@AuthenticationPrincipal JelatynaPrincipal principal,
                                                         @PathVariable String id) {
        Edition edition = repository.findById(id);
        if (edition == null) {
            return ResponseEntity.notFound().build();
        }
        if (edition.isCurrent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Edition previous = repository.findCurrent();
        int votesCleared = transactionTemplate.execute(status -> {
            int cleared = voteRepository.clearVoterIpsIn(previous);
            // At most one Edition can be current, so the previous one stops being current first.
            repository.saveAndFlush(previous.setCurrent(false));
            repository.save(edition.setCurrent(true));
            return cleared;
        });
        // Ids only: no IP addresses or emails in the logs.
        log.atInfo().setMessage("Made an Edition current")
                .addKeyValue("actorUserId", principal.getId())
                .addKeyValue("previousEditionId", previous.getId())
                .addKeyValue("editionId", edition.getId())
                .addKeyValue("votesCleared", votesCleared)
                .log();
        // The switch stands even if this dump fails: the scheduled dump retries within ten minutes.
        try {
            webpageDataDumper.dumpAll();
        } catch (Exception e) {
            log.error("Dumping Edition {} after it became current failed", edition.getId(), e);
        }
        return ResponseEntity.ok(EditionDto.from(edition));
    }

    /**
     * Whether the request has a name and the conference dates, with the last day no earlier than the
     * conference starts in Warsaw, and any C4P period ending after it starts.
     */
    private static boolean isValid(EditionRequest request) {
        if (request.name() == null || request.conferenceStart() == null || request.lastDay() == null) {
            return false;
        }
        boolean c4pEndsAfterItStarts = request.c4pStart() == null || request.c4pEnd() == null
                || request.c4pEnd().isAfter(request.c4pStart());
        return c4pEndsAfterItStarts && !request.lastDay().isBefore(LocalDate.ofInstant(request.conferenceStart(), WARSAW));
    }
}
