package pl.confitura.jelatyna.presentation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import pl.confitura.jelatyna.api.model.FullPresentation;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.presentation.rating.Rate;
import pl.confitura.jelatyna.presentation.rating.RatingService;
import pl.confitura.jelatyna.presentation.rating.RatingWindowService;
import pl.confitura.jelatyna.presentation.rating.ViewPresentationRate;
import pl.confitura.jelatyna.presentation.rating.ViewPresentationRateRepository;

import java.time.LocalDate;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@RestController
public class PresentationController {

    private final PresentationRepository repository;
    private final UserRepository userRepository;
    private final RatingService ratingService;
    private final RatingWindowService ratingWindowService;
    private final TagRepository tagRepository;
    private final ViewPresentationRateRepository ratesRepository;
    private final Security security;


    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @GetMapping("/presentations/{presentationId}/cospeakers")
    public ResponseEntity<Set<CospeakerDto>> getCospeakers(@PathVariable String presentationId) {
        Set<CospeakerDto> cospeakers = this.repository.findById(presentationId).getSpeakers().stream()
                .map(CospeakerDto::from)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(cospeakers);
    }

    @PreAuthorize("(@security.presentationOwnedByUser(#presentationId) || @security.isAdmin())"
            + " && @security.canChangePresentation(#presentationId)")
    @DeleteMapping("/presentations/{presentationId}/cospeakers/{id:.+}")
    public ResponseEntity<?> removeCospeaker(@PathVariable String presentationId, @PathVariable String id) {
        Presentation presentation = this.repository.findById(presentationId);
        if (presentation.getSpeakers().size() == 1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Presentation needs at least one speaker!");
        }
        presentation.setSpeakers(removeCospeakerById(id, presentation.getSpeakers()));
        repository.save(presentation);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("(@security.presentationOwnedByUser(#presentationId) || @security.isAdmin())"
            + " && @security.canChangePresentation(#presentationId)")
    @PostMapping("/presentations/{presentationId}/cospeakers/{email:.+}")
    @Transactional
    public ResponseEntity<?> addCospeaker(@PathVariable String presentationId, @PathVariable String email) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Presentation presentation = this.repository.findById(presentationId);
        if (presentation.isOwnedBy(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You cannot add yourself as a speaker!");
        }
        if (presentation.hasCospeaker(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This speaker is already added to this presentation");
        }
        presentation.getSpeakers().add(user);
        return ResponseEntity.ok(CospeakerDto.from(user));
    }

    private Set<User> removeCospeakerById(String id, Set<User> cospeakers) {
        return cospeakers.stream().filter(it -> !it.getId().equalsIgnoreCase(id)).collect(Collectors.toSet());
    }

    @PostMapping("/presentations/{presentationId}/ratings")
    @Transactional
    public ResponseEntity<?> addRating(
            @PathVariable String presentationId,
            @RequestBody @Valid @NotNull RateRequest rate) {
        if (rate.getReviewerToken() == null) {
            return ResponseEntity.badRequest().body("Reviewer token is required");
        }
        if (!ratingWindowService.isOpen()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Rating is not open yet");
        }
        Presentation presentation = repository.findById(presentationId);
        if (presentation == null) {
            return ResponseEntity.notFound().build();
        }
        if (!presentation.getEdition().isCurrent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the current edition's presentations can be rated");
        }
        if (!presentation.isRatingEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Rating is disabled for this presentation");
        }
        Rate createdRate = ratingService.rate(presentationId, rate.toRate(rate.getReviewerToken()));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRate);
    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @GetMapping("/presentations/{presentationId}/ratings")
    public ResponseEntity<ViewPresentationRate> rates(@PathVariable String presentationId) {
        return this.ratesRepository.findByPresentationId(presentationId)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }


    @GetMapping("/presentations/{presentationId}/rating-enabled")
    public ResponseEntity<RatingEnabledResponse> getRatingEnabled(@PathVariable String presentationId) {
        Presentation presentation = repository.findById(presentationId);
        if (presentation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new RatingEnabledResponse(presentation.isRatingEnabled()));
    }

    public record RatingEnabledResponse(boolean ratingEnabled) {
    }

    @GetMapping("/rating/status")
    public RatingStatusResponse getRatingStatus() {
        return new RatingStatusResponse(
                ratingWindowService.isOpen(),
                ratingWindowService.opensAt().orElse(null));
    }

    public record RatingStatusResponse(boolean open, LocalDate opensAt) {
    }

    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagRepository.findAll());
    }

    @GetMapping("/presentations")
    public ResponseEntity<List<FullPresentation>> getAllPresentations() {
        List<Presentation> presentations = security.isAdmin()
                ? repository.findAllInCurrentEdition()
                : repository.findAccepted();
        return ResponseEntity.ok(presentations.stream().map(FullPresentation::new).collect(toList()));
    }
}
