package pl.confitura.jelatyna.user;

import com.timgroup.jgravatar.Gravatar;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.ParticipationRepository;
import pl.confitura.jelatyna.user.dto.PublicUser;

import java.util.Set;
import java.util.UUID;

import static com.timgroup.jgravatar.GravatarDefaultImage.BLANK;
import static com.timgroup.jgravatar.GravatarRating.GENERAL_AUDIENCES;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.StringUtils.isEmpty;

@RequiredArgsConstructor
@RepositoryRestController
class UserController {

    private final UserRepository repository;
    private final ParticipationRepository participationRepository;

    @PostMapping("/users")
    @PreAuthorize("@security.isOwner(#user.id)")
    public ResponseEntity<?> save(@RequestBody User user) {
        User current = updateUser(user);
        setDefaultPhotoFor(current);
        setIdIfManuallyCreated(current);
        return ResponseEntity.ok(new Resource<>(repository.save(current)));
    }

    @PostMapping("/users/{userId}/participationData")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<?> assignParticipationData(
            @PathVariable String userId,
            @RequestBody ParticipationData participationData) {
        User current = repository.findById(userId);
        current.setParticipationData(participationRepository.findById(participationData.getId()));
        current.setParticipationData(participationRepository.findById(participationData.getId()));
        return ResponseEntity.ok(new Resource<>(repository.save(current)));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("@security.isOwner(#id) || @security.isAdmin()")
    public ResponseEntity<?> getById(@PathVariable String id) {
        User user = repository.findById(id);
        return ResponseEntity.ok(new Resource<>(user));
    }

    @GetMapping("/users/{id}/public")
    public ResponseEntity<?> getPublicById(@PathVariable String id) {
        User user = repository.findById(id);
        return ResponseEntity.ok(new Resource<>(user.toPublicUser()));
    }

    @GetMapping("/users/search/admins")
    public ResponseEntity<?> getAdmins() {
        Set<PublicUser> admins = repository.findAdmins().stream().map(User::toPublicUser).collect(toSet());
        return ResponseEntity.ok(new Resources<>(admins));
    }

    @GetMapping("/users/search/volunteers")
    public ResponseEntity<?> getVolunteers() {
        Set<PublicUser> volunteers = repository.findVolunteers().stream().map(User::toPublicUser).collect(toSet());
        return ResponseEntity.ok(new Resources<>(volunteers));
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

    @GetMapping("/users/search/speakers")
    public ResponseEntity<?> getSpeakers() {
        Set<Resource<?>> speakers = repository.findAllAccepted().stream()
                .map(User::toPublicUser)
                .map(speaker -> new Resource<>(speaker))
                .collect(toSet());
        return ResponseEntity.ok(new Resources<>(speakers));
    }

    private void setDefaultPhotoFor(@RequestBody User user) {
        if (isEmpty(user.getPhoto())) {
            Gravatar gravatar = new Gravatar(300, GENERAL_AUDIENCES, BLANK);
            String url = gravatar.getUrl(user.getEmail());
            user.setPhoto(url.replace("http:", "https:"));
        }
    }

    private User updateUser(@RequestBody User user) {
        if (isEmpty(user.getId())) {
            return user;
        } else {
            User current = repository.findById(user.getId());
            current.updateFields(user);
            return current;
        }
    }

    private void setIdIfManuallyCreated(@RequestBody User user) {
        if (StringUtils.isEmpty(user.getId())) {
            user.setId(UUID.randomUUID().toString());
        }
    }
}
