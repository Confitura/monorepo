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
import pl.confitura.jelatyna.user.dto.FullUserDto;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RepositoryRestController
@RequiredArgsConstructor
public class PresentationController {

    private final PresentationFacade presentationFacade;

    @PostMapping("/presentations/{presentationId}/accept")
    @Transactional
    public ResponseEntity<?> accept(@PathVariable String presentationId) {
        this.presentationFacade.accept(presentationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/presentations/{presentationId}/unaccept")
    @Transactional
    public ResponseEntity<?> unaccept(@PathVariable String presentationId) {
        this.presentationFacade.unaccept(presentationId);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/presentations/{presentationId}/speakers")
    public ResponseEntity<Resources<SpeakerEntity>> getSpeakers(@PathVariable String presentationId) {
        Set<SpeakerEntity> speakers = presentationFacade.getSpeakers(presentationId);
        return ResponseEntity.ok(new Resources<>(speakers));
    }

    @DeleteMapping("/presentations/{presentationId}/speakers/{id:.+}")
    public ResponseEntity<?> removeSpeaker(@PathVariable String presentationId, @PathVariable String id) {
        presentationFacade.removeSpeaker(presentationId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/presentations/{presentationId}/speakers/{email:.+}")
    @Transactional
    public ResponseEntity<?> addSpeaker(@PathVariable String presentationId, @PathVariable String email) {
        Presentation presentation = presentationFacade.addSpeakerByEmail(presentationId, email);
        return ResponseEntity.ok(presentation);
    }

    @PostMapping("/users/{userId}/presentations")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<?> savePresentation(@Valid @RequestBody Presentation presentation,
                                              @PathVariable String userId) {
        Presentation saved = presentationFacade.savePresentation(presentation, userId);
        return ResponseEntity.ok(new Resource<>(saved));
    }

}
