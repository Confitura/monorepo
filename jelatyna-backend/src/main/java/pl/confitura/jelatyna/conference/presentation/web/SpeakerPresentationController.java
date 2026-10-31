package pl.confitura.jelatyna.conference.presentation.web;

import static java.util.function.Predicate.not;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import pl.confitura.jelatyna.api.model.InlinePresentation;
import pl.confitura.jelatyna.api.model.InlineWorkshop;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.api.model.WorkshopRequest;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.presentation.Tag;
import pl.confitura.jelatyna.presentation.TagRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

/** A speaker's own talks and workshops, under their /users/{id} path. */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SpeakerPresentationController {

    private final UserRepository userRepository;
    private final PresentationRepository presentationRepository;
    private final Security security;
    private final EditionRepository editionRepository;
    private final TagRepository tagRepository;

    @GetMapping("/users/{id}/presentations")
    @PreAuthorize("@security.isOwner(#id)")
    public ResponseEntity<List<InlinePresentation>> getUserPresentations(@PathVariable String id,
                                                                         @RequestParam(required = false) String editionId) {
        User user = userRepository.findById(id);
        List<InlinePresentation> presentations = presentationRepository.findBySpeakersContainsAndEdition(user, editionOrCurrent(editionId))
                .stream()
                .filter(not(Presentation::isWorkshop))
                .map(InlinePresentation::new)
                .toList();
        return ResponseEntity.ok(presentations);
    }

    @GetMapping("/users/{id}/presentations/{presentationId}")
    @PreAuthorize("@security.isOwner(#id) && @security.presentationOwnedByUser(#presentationId)"
            + " && !@security.isWorkshop(#presentationId)")
    public ResponseEntity<InlinePresentation> getPresentation(@PathVariable String id, @PathVariable String presentationId) {
        var presentation = presentationRepository.findById(presentationId);
        return ResponseEntity.ok(new InlinePresentation(presentation));
    }

    @PostMapping("/users/{userId}/presentations")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<InlinePresentation> addPresentationToUser(@Valid @RequestBody PresentationRequest presentationRequest,
                                                                    @PathVariable String userId) {

        if (!canCreatePresentation()) {
            log.info("User {} tried to add presentation after v4p is closed", userId);
            return ResponseEntity.status(FORBIDDEN).build();
        }

        User speaker = userRepository.findById(userId);
        Set<Tag> tags = getTags(presentationRequest.tags());
        var presentation = Presentation.from(presentationRequest, speaker, tags);
        presentation.setSpeaker(speaker);
        presentation.setEdition(editionRepository.findCurrent());
        retainStatus(presentation);
        Presentation saved = presentationRepository.save(presentation);
        return ResponseEntity.ok(new InlinePresentation(saved));
    }


    @PostMapping("/users/{userId}/presentations/{presentationId}")
    @PreAuthorize("@security.isOwner(#userId) && @security.presentationOwnedByUser(#presentationId)"
            + " && !@security.isWorkshop(#presentationId)"
            + " && @security.canChangePresentation(#presentationId)")
    public ResponseEntity<InlinePresentation> updatePresentation(@Valid @RequestBody PresentationRequest presentationRequest,
                                                                 @PathVariable String userId,
                                                                 @PathVariable String presentationId) {
        Set<Tag> tags = getTags(presentationRequest.tags());

        var presentation = presentationRepository.findById(presentationId);
        presentation.update(presentationRequest, tags);
        Presentation saved = presentationRepository.save(presentation);
        return ResponseEntity.ok(new InlinePresentation(saved));
    }

    @DeleteMapping("/users/{userId}/presentations/{presentationId}")
    @PreAuthorize("@security.isOwner(#userId) && @security.presentationOwnedByUser(#presentationId)"
            + " && !@security.isWorkshop(#presentationId)"
            + " && @security.canChangePresentation(#presentationId)")
    public void deletePresentation(@PathVariable String userId, @PathVariable String presentationId) {
        presentationRepository.deleteById(presentationId);
    }

    @PostMapping("/users/{userId}/presentations/{presentationId}/resubmit")
    @PreAuthorize("@security.isOwner(#userId) && @security.presentationOwnedByUser(#presentationId)"
            + " && !@security.isWorkshop(#presentationId)")
    public ResponseEntity<InlinePresentation> resubmitPresentation(@PathVariable String userId,
                                                                   @PathVariable String presentationId) {
        return ResponseEntity.ok(new InlinePresentation(resubmit(userId, presentationId)));
    }

    @PostMapping("/users/{userId}/workshops")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<InlineWorkshop> addWorkshopToUser(@Valid @RequestBody WorkshopRequest workshopRequest,
                                                            @PathVariable String userId) {

        if (!canCreatePresentation()) {
            log.info("User {} tried to add presentation after v4p is closed", userId);
            return ResponseEntity.status(FORBIDDEN).build();
        }

        User speaker = userRepository.findById(userId);
        Set<Tag> tags = getTags(workshopRequest.tags());
        var workshop = Presentation.from(workshopRequest, speaker, tags);
        workshop.setSpeaker(speaker);
        workshop.setEdition(editionRepository.findCurrent());
        retainStatus(workshop);
        Presentation saved = presentationRepository.save(workshop);
        return ResponseEntity.ok(new InlineWorkshop(saved));
    }


    @PostMapping("/users/{userId}/workshops/{workshopId}")
    @PreAuthorize("@security.isOwner(#userId) && @security.presentationOwnedByUser(#workshopId)"
            + " && @security.isWorkshop(#workshopId)"
            + " && @security.canChangePresentation(#workshopId)")
    public ResponseEntity<InlineWorkshop> updateWorkshop(@Valid @RequestBody WorkshopRequest workshopRequest,
                                                         @PathVariable String userId,
                                                         @PathVariable String workshopId) {
        Set<Tag> tags = getTags(workshopRequest.tags());

        var workshop = presentationRepository.findById(workshopId);
        workshop.update(workshopRequest, tags);
        Presentation saved = presentationRepository.save(workshop);
        return ResponseEntity.ok(new InlineWorkshop(saved));
    }

    @DeleteMapping("/users/{userId}/workshops/{workshopId}")
    @PreAuthorize("@security.isOwner(#userId) && @security.presentationOwnedByUser(#workshopId)"
            + " && @security.isWorkshop(#workshopId)"
            + " && @security.canChangePresentation(#workshopId)")
    public void deleteWorkshop(@PathVariable String userId, @PathVariable String workshopId) {
        presentationRepository.deleteById(workshopId);
    }

    @PostMapping("/users/{userId}/workshops/{workshopId}/resubmit")
    @PreAuthorize("@security.isOwner(#userId) && @security.presentationOwnedByUser(#workshopId)"
            + " && @security.isWorkshop(#workshopId)")
    public ResponseEntity<InlineWorkshop> resubmitWorkshop(@PathVariable String userId, @PathVariable String workshopId) {
        return ResponseEntity.ok(new InlineWorkshop(resubmit(userId, workshopId)));
    }

    @GetMapping("/users/{id}/workshops")
    @PreAuthorize("@security.isOwner(#id)")
    public ResponseEntity<List<InlineWorkshop>> getUserWorkshops(@PathVariable String id,
                                                                 @RequestParam(required = false) String editionId) {
        var user = userRepository.getReferenceById(id);
        var presentations = presentationRepository.findBySpeakersContainsAndEdition(user, editionOrCurrent(editionId))
                .stream()
                .filter(Presentation::isWorkshop)
                .map(InlineWorkshop::new)
                .toList();
        return ResponseEntity.ok(presentations);
    }

    @GetMapping("/users/{id}/workshops/{workshopId}")
    @PreAuthorize("@security.isOwner(#id) && @security.presentationOwnedByUser(#workshopId)"
            + " && @security.isWorkshop(#workshopId)")
    public ResponseEntity<InlineWorkshop> getWorkshop(@PathVariable String id, @PathVariable String workshopId) {
        var workshop = presentationRepository.findById(workshopId);
        return ResponseEntity.ok(new InlineWorkshop(workshop));
    }

    @NotNull
    private Set<Tag> getTags(String[] tags) {
        return Stream.of(tags)
                .map(tagRepository::getReferenceById)
                .collect(Collectors.toSet());
    }

    private void retainStatus(Presentation presentation) {
        if (!presentation.isNew()) {
            Presentation saved = presentationRepository.findById(presentation.getId());
            presentation.setStatus(saved.getStatus());
        }
    }

    // The speaker homepage opens on the Current Edition (ADR-0004).
    private Edition editionOrCurrent(String editionId) {
        return editionId == null ? editionRepository.findCurrent() : editionRepository.findById(editionId);
    }

    private boolean canCreatePresentation() {
        return editionRepository.findCurrent().isC4pOpen() || security.isAdmin();
    }

    // Submit again (ADR-0004): copies a past Edition's talk or workshop into the Current Edition,
    // with the user as its only speaker. A Current Edition's talk can still be edited, so it is refused.
    private Presentation resubmit(String userId, String presentationId) {
        if (!canCreatePresentation()) {
            log.info("User {} tried to resubmit presentation after v4p is closed", userId);
            throw new ResponseStatusException(FORBIDDEN);
        }
        Presentation original = presentationRepository.findById(presentationId);
        if (original.getEdition().isCurrent()) {
            throw new ResponseStatusException(CONFLICT, "Only a past Edition's talk can be submitted again");
        }
        Presentation copy = original.resubmittedBy(userRepository.findById(userId));
        copy.setEdition(editionRepository.findCurrent());
        return presentationRepository.save(copy);
    }
}
