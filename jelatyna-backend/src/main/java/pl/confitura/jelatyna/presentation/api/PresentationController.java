package pl.confitura.jelatyna.presentation.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.presentation.PresentationFacade;
import pl.confitura.jelatyna.presentation.Speaker;
import pl.confitura.jelatyna.presentation.dto.Presentation;
import pl.confitura.jelatyna.presentation.rating.RatingService;

import javax.validation.Valid;
import java.util.Set;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RepositoryRestController
@RequiredArgsConstructor
class PresentationController {

    private final PresentationFacade facade;

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/accept")
    @Transactional
    public void accept(@PathVariable String presentationId) {
        this.facade.accept(presentationId);
    }

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/unaccept")
    @Transactional
    public void unaccept(@PathVariable String presentationId) {
        facade.unaccept(presentationId);
    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @GetMapping("/presentations/{presentationId}/speakers")
    public Set<Speaker> getSpeakers(@PathVariable String presentationId) {
        return this.facade.getSpeakers(presentationId);
    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @DeleteMapping("/presentations/{presentationId}/speakers/{id:.+}")
    public ResponseEntity<?> removeSpeaker(@PathVariable String presentationId, @PathVariable String id) {
        PresentationFacade.RemoveSpeakerStatus status = this.facade.removeSpeakerFromPresentation(presentationId, id);
        if (status == PresentationFacade.RemoveSpeakerStatus.REMOVING_LAST_SPEAKER) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Presentation needs at least one speaker!");
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/speakers/{email:.+}")
    @Transactional
    public ResponseEntity<?> addSpeaker(@PathVariable String presentationId, @PathVariable String email) {
        PresentationFacade.AddSpeakerStatus status = facade.addSpeaker(presentationId, email);
        if (status == PresentationFacade.AddSpeakerStatus.SPEAKER_NOT_EXISTS) {
            return ResponseEntity.notFound().build();
        } else if (status == PresentationFacade.AddSpeakerStatus.SPEAKER_ALREADY_ADDED) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This speaker is already added to this presentation");
        } else {
            return ResponseEntity.ok().build();
        }
    }


    @PostMapping("/users/{userId}/presentations")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<?> submitPresentation(@Valid @RequestBody Presentation presentation,
                                                @PathVariable String userId) {
        PresentationFacade.SubmitPresentationStatus status = facade.submitPresentation(presentation, userId);
        if (status == PresentationFacade.SubmitPresentationStatus.CALL_FOR_PAPERS_CLOSED) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }



}
