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
import pl.confitura.jelatyna.presentation.rating.Rate;
import pl.confitura.jelatyna.presentation.rating.RatingService;
import pl.confitura.jelatyna.presentation.rating.ViewPresentationRate;
import pl.confitura.jelatyna.presentation.rating.ViewPresentationRateRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@RestController
public class PresentationController {

    private final PresentationRepository repository;
    private final UserRepository userRepository;
    private final RatingService ratingService;
    private final TagRepository tagRepository;
    private final ViewPresentationRateRepository ratesRepository;


    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @GetMapping("/presentations/{presentationId}/cospeakers")
    public ResponseEntity<Set<User>> getCospeakers(@PathVariable String presentationId) {
        Set<User> cospeakers = this.repository.findById(presentationId).getSpeakers();
        return ResponseEntity.ok(cospeakers);
    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
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

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
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
        return ResponseEntity.ok(user);
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
        Rate createdRate = ratingService.rate(presentationId, rate.toRate(rate.getReviewerToken()));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRate);
    }

    @PutMapping("/presentations/{presentationId}/ratings/{ratingId}")
    @Transactional
    public ResponseEntity<?> updateRating(
            @PathVariable("presentationId") String presentationId,
            @PathVariable("ratingId") String ratingId,
            @RequestBody @Valid Rate rate) {
        ratingService.updateRating(rate.setId(ratingId));
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @GetMapping("/presentations/{presentationId}/ratings")
    public ResponseEntity<ViewPresentationRate> rates(@PathVariable String presentationId) {
        return this.ratesRepository.findByPresentationId(presentationId)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }


    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagRepository.findAll());
    }

    @GetMapping("/presentations")
    public ResponseEntity<List<FullPresentation>> getAllPresentations() {
        return ResponseEntity.ok(repository.findAll().stream().map(FullPresentation::new).collect(toList()));
    }
}
