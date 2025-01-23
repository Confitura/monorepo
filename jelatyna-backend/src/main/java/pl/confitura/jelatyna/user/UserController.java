package pl.confitura.jelatyna.user;

import static com.timgroup.jgravatar.GravatarDefaultImage.BLANK;
import static com.timgroup.jgravatar.GravatarRating.GENERAL_AUDIENCES;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.StringUtils.hasText;

import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.timgroup.jgravatar.Gravatar;
import lombok.RequiredArgsConstructor;
import pl.confitura.jelatyna.ConferenceConfigurationProperties;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.ParticipationRepository;

@RequiredArgsConstructor
@RepositoryRestController
public class UserController {

    private final UserRepository repository;
    private final PresentationRepository presentationRepository;
    private final Security security;
    private final ConferenceConfigurationProperties conferenceConfiguration;
    private final ParticipationRepository participationRepository;

    @PostMapping("/users")
    @PreAuthorize("@security.isOwner(#user.id)")
    public ResponseEntity<?> save(@RequestBody User user) {
        User current = updateUser(user);
        setDefaultPhotoFor(current);
        setIdIfManuallyCreated(current);
        return ResponseEntity.ok(EntityModel.of(repository.save(current)));
    }

    @PostMapping("/users/{userId}/participationData")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<?> assignParticipationData(
            @PathVariable String userId,
            @RequestBody ParticipationData participationData) {
        User current = repository.findById(userId);
        current.setParticipationData(participationRepository.findById(participationData.getId()));
        current.setParticipationData(participationRepository.findById(participationData.getId()));
        return ResponseEntity.ok(EntityModel.of(repository.save(current)));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("@security.isOwner(#id) || @security.isAdmin()")
    public ResponseEntity<?> getById(@PathVariable String id) {
        User user = repository.findById(id);
        return ResponseEntity.ok(EntityModel.of(user));
    }

    @GetMapping("/users/{id}/public")
    public ResponseEntity<?> getPublicById(@PathVariable String id) {
        User user = repository.findById(id);
        return ResponseEntity.ok(EntityModel.of(new PublicUser(user)));
    }

    @GetMapping("/users/search/admins")
    public ResponseEntity<?> getAdmins() {
        Set<PublicUser> admins = repository.findAdmins().stream().map(PublicUser::new).collect(toSet());
        return ResponseEntity.ok(CollectionModel.of(admins));
    }

    @GetMapping("/users/search/volunteers")
    public ResponseEntity<?> getVolunteers() {
        Set<PublicUser> volunteers = repository.findVolunteers().stream().map(PublicUser::new).collect(toSet());
        return ResponseEntity.ok(CollectionModel.of(volunteers));
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
    public ResponseEntity<?> addPresentationToUser(@Valid @RequestBody Presentation presentation,
            @PathVariable String userId) {
        if (presentation.isNew() && !canCreatePresentation()) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
        User speaker = repository.findById(userId);
        presentation.setSpeaker(speaker);
        retainStatus(presentation);
        Presentation saved = presentationRepository.save(presentation);
        return ResponseEntity.ok(EntityModel.of(saved));
    }

    @GetMapping("/users/search/speakers")
    public ResponseEntity<?> getSpeakers() {
        Set<PublicUser> speakers = repository.findAllAccepted().stream()
                .map(PublicUser::new)
                .collect(toSet());
        return ResponseEntity.ok(CollectionModel.of(speakers));
    }

    private void retainStatus(Presentation presentation) {
        if (!presentation.isNew()) {
            Presentation saved = presentationRepository.findById(presentation.getId());
            presentation.setStatus(saved.getStatus());
        }
    }

    private void setDefaultPhotoFor(@RequestBody User user) {
        if (hasText(user.getPhoto())) {
            Gravatar gravatar = new Gravatar(300, GENERAL_AUDIENCES, BLANK);
            String url = gravatar.getUrl(user.getEmail());
            user.setPhoto(url.replace("http:", "https:"));
        }
    }

    private boolean canCreatePresentation() {
        return conferenceConfiguration.getC4p().isEnabled() || security.isAdmin();
    }

    private User updateUser(@RequestBody User user) {
        if (hasText(user.getId())) {
            return user;
        } else {
            User current = repository.findById(user.getId());
            current.updateFields(user);
            return current;
        }
    }

    private void setIdIfManuallyCreated(@RequestBody User user) {
        if (hasText(user.getId())) {
            user.setId(UUID.randomUUID().toString());
        }
    }
}
