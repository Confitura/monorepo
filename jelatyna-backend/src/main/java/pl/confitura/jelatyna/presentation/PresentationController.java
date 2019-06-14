package pl.confitura.jelatyna.presentation;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.AllArgsConstructor;
import pl.confitura.jelatyna.presentation.rating.Rate;
import pl.confitura.jelatyna.presentation.rating.RatingService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

@RepositoryRestController
@AllArgsConstructor
public class PresentationController {

    private PresentationRepository repository;
    private UserRepository userRepository;
    private RatingService ratingService;

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/accept")
    @Transactional
    public ResponseEntity<?> accept(@PathVariable String presentationId) {
        this.repository.findById(presentationId).setAccepted(true);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/unaccept")
    @Transactional
    public ResponseEntity<?> unaccept(@PathVariable String presentationId) {
        this.repository.findById(presentationId).setAccepted(false);
        return ResponseEntity.ok().build();

    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @GetMapping("/presentations/{presentationId}/cospeakers")
    public ResponseEntity<Resources<User>> getCospeakers(@PathVariable String presentationId) {
        Set<User> cospeakers = this.repository.findById(presentationId).getSpeakers();
        return ResponseEntity.ok(new Resources<>(cospeakers));
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

    private Set<User> removeCospeakerByEmail(String email, Set<User> cospeakers) {
        return cospeakers.stream().filter(it -> !it.getEmail().equalsIgnoreCase(email)).collect(Collectors.toSet());
    }

    private Set<User> removeCospeakerById(String id, Set<User> cospeakers) {
        return cospeakers.stream().filter(it -> !it.getId().equalsIgnoreCase(id)).collect(Collectors.toSet());
    }

    @PostMapping("/presentations/{presentationId}/ratings")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<?>  addRating(@PathVariable String presentationId, @RequestBody @Valid Rate rate){
        Rate createdRate = ratingService.rate(presentationId, rate);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRate);
    }

    @PutMapping("/presentations/{presentationId}/ratings/{ratingId}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<?> updateRating(@PathVariable("ratingId") String ratingId, @RequestBody @Valid Rate rate) {
        ratingService.updateRating(rate.setId(ratingId));
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
