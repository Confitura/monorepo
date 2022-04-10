package pl.confitura.jelatyna.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.ConferenceConfigurationProperties;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.presentation.rating.Rate;
import pl.confitura.jelatyna.presentation.rating.RatingService;
import pl.confitura.jelatyna.user.UserFacade;
import pl.confitura.jelatyna.user.dto.User;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RepositoryRestController
@RequiredArgsConstructor
public class PresentationController {

    private final PresentationRepository repository;
    private final UserFacade userFacade;
    private final RatingService ratingService;
    private final ConferenceConfigurationProperties conferenceConfiguration;
    private final Security security;

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/accept")
    @Transactional
    public ResponseEntity<?> accept(@PathVariable String presentationId) {
        this.repository.findById(presentationId).setAccepted(true);
        Presentation presentation = this.repository.findById(presentationId);
        Set<Speaker> speakers = presentation.getSpeakers();
        for (Speaker speaker : speakers) {
            userFacade.markSpeaker(speaker.getId(), true);
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/unaccept")
    @Transactional
    public ResponseEntity<?> unaccept(@PathVariable String presentationId) {
        Presentation presentation = this.repository.findById(presentationId);
        presentation.setAccepted(false);
        repository.save(presentation);
        Set<Speaker> speakers = presentation.getSpeakers();
        for (Speaker speaker : speakers) {
            Long accepted = repository.countAcceptedWithSpeaker(speaker);
            boolean isSpeaker = accepted > 0;
            userFacade.markSpeaker(speaker.getId(), isSpeaker);
        }
        return ResponseEntity.ok().build();

    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @GetMapping("/presentations/{presentationId}/cospeakers")
    public ResponseEntity<Resources<Speaker>> getCospeakers(@PathVariable String presentationId) {
        Set<Speaker> cospeakers = this.repository.findById(presentationId).getSpeakers();
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
        User user = this.userFacade.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Presentation presentation = this.repository.findById(presentationId);
        if (presentation.isOwnedBy(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This speaker is already added to this presentation");
        }
        presentation.getSpeakers().add(Speaker.fromUser(user));
        return ResponseEntity.ok(presentation);
    }

    private Set<Speaker> removeCospeakerById(String id, Set<Speaker> cospeakers) {
        return cospeakers.stream().filter(it -> !it.getId().equalsIgnoreCase(id)).collect(Collectors.toSet());
    }

    @PostMapping("/presentations/{presentationId}/ratings")
    @PreAuthorize("@security.isAuthenticated()")
    @Transactional
    public ResponseEntity<?>  addRating(@PathVariable String presentationId, @RequestBody @Valid Rate rate){
        Rate createdRate = ratingService.rate(presentationId, rate);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRate);
    }

    @PutMapping("/presentations/{presentationId}/ratings/{ratingId}")
    @PreAuthorize("@security.isAuthenticated()")
    @Transactional
    public ResponseEntity<?> updateRating(@PathVariable("ratingId") String ratingId, @RequestBody @Valid Rate rate) {
        ratingService.updateRating(rate.setId(ratingId));
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }


    @PostMapping("/users/{userId}/presentations")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<?> addPresentationToUser(@Valid @RequestBody Presentation presentation,
                                                   @PathVariable String userId) {
        if (presentation.isNew() && !canCreatePresentation()) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
        User speaker = userFacade.findById(userId);
        presentation.setSpeaker(Speaker.fromUser(speaker));
        retainStatus(presentation);
        Presentation saved = repository.save(presentation);
        return ResponseEntity.ok(new Resource<>(saved));
    }

    private boolean canCreatePresentation() {
        return conferenceConfiguration.getC4p().isEnabled() || security.isAdmin();
    }

    private void retainStatus(Presentation presentation) {
        if (!presentation.isNew()) {
            Presentation saved = repository.findById(presentation.getId());
            presentation.setStatus(saved.getStatus());
        }
    }



}
