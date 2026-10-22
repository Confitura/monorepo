package pl.confitura.jelatyna.edition;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.confitura.jelatyna.agenda.DayRepository;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Lets admins manage Editions; every endpoint is admin-only. None makes an Edition current: the backend
 * needs exactly one Current Edition, so a new Edition is never current and the current one is never deleted.
 */
@RestController
@RequestMapping("/editions")
@PreAuthorize("@security.isAdmin()")
@RequiredArgsConstructor
public class EditionController {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

    private final EditionRepository repository;
    private final PresentationRepository presentationRepository;
    private final DayRepository dayRepository;

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
