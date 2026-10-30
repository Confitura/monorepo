package pl.confitura.jelatyna.user;

import static com.timgroup.jgravatar.GravatarDefaultImage.BLANK;
import static com.timgroup.jgravatar.GravatarRating.GENERAL_AUDIENCES;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.StringUtils.hasText;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.timgroup.jgravatar.Gravatar;
import lombok.RequiredArgsConstructor;
import pl.confitura.jelatyna.api.model.InlinePresentation;
import pl.confitura.jelatyna.api.model.InlineWorkshop;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.api.model.WorkshopRequest;
import pl.confitura.jelatyna.edition.Edition;
import pl.confitura.jelatyna.edition.EditionDto;
import pl.confitura.jelatyna.edition.EditionRepository;
import pl.confitura.jelatyna.platform.security.JelatynaPrincipal;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.presentation.Tag;
import pl.confitura.jelatyna.presentation.TagRepository;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.ParticipationRepository;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository repository;
    private final PresentationRepository presentationRepository;
    private final Security security;
    private final EditionRepository editionRepository;
    private final ParticipationRepository participationRepository;
    private final TagRepository tagRepository;

    @PostMapping("/users")
    @PreAuthorize("@security.isOwner(#user.id)")
    public ResponseEntity<?> save(@RequestBody User user) {
        User current = updateUser(user);
        setDefaultPhotoFor(current);
        setIdIfManuallyCreated(current);
        return ResponseEntity.ok(repository.save(current));
    }

    @PostMapping("/users/{userId}/participationData")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<?> assignParticipationData(
            @PathVariable String userId,
            @RequestBody ParticipationData participationData) {
        User current = repository.findById(userId);
        current.setParticipationData(participationRepository.findById(participationData.getId()));
        return ResponseEntity.ok(repository.save(current));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("@security.isOwner(#id) || @security.isAdmin()")
    public ResponseEntity<User> getById(@PathVariable String id) {
        User user = repository.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{id}/public")
    public ResponseEntity<?> getPublicById(@PathVariable String id) {
        User user = repository.findById(id);
        return ResponseEntity.ok(PublicSpeaker.from(user));
    }

    @GetMapping("/users/{id}/presentations")
    @PreAuthorize("@security.isOwner(#id)")
    public ResponseEntity<List<InlinePresentation>> getUserPresentations(@PathVariable String id,
                                                                         @RequestParam(required = false) String editionId) {
        User user = repository.findById(id);
        List<InlinePresentation> presentations = presentationRepository.findBySpeakersContainsAndEdition(user, editionOrCurrent(editionId))
                .stream()
                .filter(not(Presentation::isWorkshop))
                .map(InlinePresentation::new)
                .toList();
        return ResponseEntity.ok(presentations);
    }

    /** The Current Edition and every Edition the user has a talk or workshop in, newest year first. */
    @GetMapping("/users/{id}/editions")
    @PreAuthorize("@security.isOwner(#id)")
    public List<EditionDto> getUserEditions(@PathVariable String id) {
        return editionRepository.findCurrentAndSpokenInBy(id).stream().map(EditionDto::from).toList();
    }

    @GetMapping("/users/{id}/presentations/{presentationId}")
    @PreAuthorize("@security.isOwner(#id) && @security.presentationOwnedByUser(#presentationId)"
            + " && !@security.isWorkshop(#presentationId)")
    public ResponseEntity<InlinePresentation> getPresentation(@PathVariable String id, @PathVariable String presentationId) {
        var presentation = presentationRepository.findById(presentationId);
        return ResponseEntity.ok(new InlinePresentation(presentation));
    }

    @GetMapping("/users/search/admins")
    public ResponseEntity<?> getAdmins() {
        Set<PublicProfile> admins = repository.findAdmins().stream().map(PublicProfile::from).collect(toSet());
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/users/search/volunteers")
    public ResponseEntity<?> getVolunteers() {
        Set<PublicProfile> volunteers = repository.findVolunteers().stream().map(PublicProfile::from).collect(toSet());
        return ResponseEntity.ok(volunteers);
    }

    @PostMapping("/users/{userId}/presentations")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<InlinePresentation> addPresentationToUser(@Valid @RequestBody PresentationRequest presentationRequest,
                                                                    @PathVariable String userId) {

        if (!canCreatePresentation()) {
            log.info("User {} tried to add presentation after v4p is closed", userId);
            return ResponseEntity.status(FORBIDDEN).build();
        }

        User speaker = repository.findById(userId);
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

        User speaker = repository.findById(userId);
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
        var user = repository.getReferenceById(id);
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


    @GetMapping("/users/search/speakers")
    public ResponseEntity<Set<PublicSpeaker>> getSpeakers() {
        var speakers = repository.findAllAccepted().stream()
                .map(PublicSpeaker::from)
                .collect(toSet());
        return ResponseEntity.ok(speakers);
    }

    @GetMapping("/current-user")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal JelatynaPrincipal user) {
        if (user == null) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(repository.findById(user.getId()));
    }

    private void retainStatus(Presentation presentation) {
        if (!presentation.isNew()) {
            Presentation saved = presentationRepository.findById(presentation.getId());
            presentation.setStatus(saved.getStatus());
        }
    }

    private void setDefaultPhotoFor(User user) {
        if (!hasText(user.getPhoto())) {
            Gravatar gravatar = new Gravatar(300, GENERAL_AUDIENCES, BLANK);
            String url = gravatar.getUrl(user.getEmail());
            user.setPhoto(url.replace("http:", "https:"));
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
        Presentation copy = original.resubmittedBy(repository.findById(userId));
        copy.setEdition(editionRepository.findCurrent());
        return presentationRepository.save(copy);
    }

    private User updateUser(User user) {
        if (!hasText(user.getId())) {
            return user;
        } else {
            User current = repository.findById(user.getId());
            current.updateFields(user);
            return current;
        }
    }

    private void setIdIfManuallyCreated(User user) {
        if (!hasText(user.getId())) {
            user.setId(UUID.randomUUID().toString());
        }
    }

}
