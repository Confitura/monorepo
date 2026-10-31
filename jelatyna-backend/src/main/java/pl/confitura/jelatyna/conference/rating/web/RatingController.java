package pl.confitura.jelatyna.conference.rating.web;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import pl.confitura.jelatyna.conference.rating.domain.Rate;
import pl.confitura.jelatyna.conference.rating.domain.RatingService;
import pl.confitura.jelatyna.conference.rating.domain.RatingWindowService;
import pl.confitura.jelatyna.conference.rating.domain.ViewPresentationRate;
import pl.confitura.jelatyna.conference.rating.domain.ViewPresentationRateRepository;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

// The tag these endpoints had while PresentationController served them, so openapi.json stays the same.
@Tag(name = "presentation-controller")
@RequiredArgsConstructor
@RestController
public class RatingController {

    private final PresentationRepository repository;
    private final RatingService ratingService;
    private final RatingWindowService ratingWindowService;
    private final ViewPresentationRateRepository ratesRepository;

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

    @GetMapping("/rating/status")
    public RatingStatusResponse getRatingStatus() {
        return new RatingStatusResponse(
                ratingWindowService.isOpen(),
                ratingWindowService.opensAt().orElse(null));
    }

    public record RatingStatusResponse(boolean open, LocalDate opensAt) {
    }
}
