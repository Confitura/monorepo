package pl.confitura.jelatyna.user;

import static com.timgroup.jgravatar.GravatarDefaultImage.BLANK;
import static com.timgroup.jgravatar.GravatarRating.GENERAL_AUDIENCES;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.StringUtils.hasText;

import java.util.List;
import java.util.Optional;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.timgroup.jgravatar.Gravatar;
import lombok.RequiredArgsConstructor;
import pl.confitura.jelatyna.ConferenceConfigurationProperties;
import pl.confitura.jelatyna.api.model.InlinePresentation;
import pl.confitura.jelatyna.api.model.InlineWorkshop;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.api.model.WorkshopRequest;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
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
    private final ConferenceConfigurationProperties conferenceConfiguration;
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
        return ResponseEntity.ok(new PublicUser(user));
    }

    @GetMapping("/users/{id}/presentations")
    @PreAuthorize("@security.isOwner(#id)")
    public ResponseEntity<List<InlinePresentation>> getUserPresentations(@PathVariable String id) {
        User user = repository.findById(id);
        List<InlinePresentation> presentations = presentationRepository.findBySpeakersContains(user)
                .stream()
                .filter(not(Presentation::isWorkshop))
                .map(InlinePresentation::new)
                .toList();
        return ResponseEntity.ok(presentations);
    }

    @GetMapping("/users/{id}/presentations/{presentationId}")
    @PreAuthorize("@security.isOwner(#id)")
    public ResponseEntity<InlinePresentation> getPresentation(@PathVariable String id, @PathVariable String presentationId) {
        var presentation = presentationRepository.findById(presentationId);
        return ResponseEntity.ok(new InlinePresentation(presentation));
    }

    @GetMapping("/users/search/admins")
    public ResponseEntity<?> getAdmins() {
        Set<PublicUser> admins = repository.findAdmins().stream().map(PublicUser::new).collect(toSet());
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/users/search/volunteers")
    public ResponseEntity<?> getVolunteers() {
        Set<PublicUser> volunteers = repository.findVolunteers().stream().map(PublicUser::new).collect(toSet());
        return ResponseEntity.ok(volunteers);
    }

    @PostMapping("/users/{userId}/volunteer/{isVolunteer}")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> markAsVolunteer(@PathVariable("userId") String userId,
                                                  @PathVariable("isVolunteer") boolean isVolunteer) {
        User user = repository.findById(userId);
        user.setVolunteer(isVolunteer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/presentations")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<InlinePresentation> addPresentationToUser(@Valid @RequestBody PresentationRequest presentationRequest,
                                                                    @PathVariable String userId) {

        if (!canCreatePresentation()) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        User speaker = repository.findById(userId);
        Set<Tag> tags = getTags(presentationRequest.tags());
        var presentation = Presentation.from(presentationRequest, speaker, tags);
        presentation.setSpeaker(speaker);
        retainStatus(presentation);
        Presentation saved = presentationRepository.save(presentation);
        return ResponseEntity.ok(new InlinePresentation(saved));
    }


    @PostMapping("/users/{userId}/presentations/{presentationId}")
    @PreAuthorize("@security.isOwner(#userId)")
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
    @PreAuthorize("@security.isOwner(#userId)")
    public void deletePresentation(@PathVariable String userId, @PathVariable String presentationId) {
        presentationRepository.deleteById(presentationId);
    }

    @PostMapping("/users/{userId}/workshops")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<InlineWorkshop> addWorkshopToUser(@Valid @RequestBody WorkshopRequest workshopRequest,
                                                            @PathVariable String userId) {

        if (!canCreatePresentation()) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        User speaker = repository.findById(userId);
        Set<Tag> tags = getTags(workshopRequest.tags());
        var workshop = Presentation.from(workshopRequest, speaker, tags);
        workshop.setSpeaker(speaker);
        retainStatus(workshop);
        Presentation saved = presentationRepository.save(workshop);
        return ResponseEntity.ok(new InlineWorkshop(saved));
    }


    @PostMapping("/users/{userId}/workshops/{workshopId}")
    @PreAuthorize("@security.isOwner(#userId)")
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
    @PreAuthorize("@security.isOwner(#userId)")
    public void deleteWorkshop(@PathVariable String userId, @PathVariable String workshopId) {
        presentationRepository.deleteById(workshopId);
    }

    @GetMapping("/users/{id}/workshops")
    @PreAuthorize("@security.isOwner(#id)")
    public ResponseEntity<List<InlineWorkshop>> getUserWorkshops(@PathVariable String id) {
        var user = repository.getReferenceById(id);
        var presentations = presentationRepository.findBySpeakersContains(user)
                .stream()
                .filter(Presentation::isWorkshop)
                .map(InlineWorkshop::new)
                .toList();
        return ResponseEntity.ok(presentations);
    }

    @GetMapping("/users/{id}/workshops/{workshopId}")
    @PreAuthorize("@security.isOwner(#id)")
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
    public ResponseEntity<?> getSpeakers() {
        Set<?> speakers = repository.findAllAccepted().stream()
                .map(PublicUser::new)
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

    private boolean canCreatePresentation() {
        return conferenceConfiguration.getC4p().isEnabled() || security.isAdmin();
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
