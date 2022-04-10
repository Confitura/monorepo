package pl.confitura.jelatyna.user.api;

import com.timgroup.jgravatar.Gravatar;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.user.UserFacade;
import pl.confitura.jelatyna.user.dto.PublicUser;
import pl.confitura.jelatyna.user.dto.User;

import java.util.Set;
import java.util.UUID;

import static com.timgroup.jgravatar.GravatarDefaultImage.BLANK;
import static com.timgroup.jgravatar.GravatarRating.GENERAL_AUDIENCES;
import static org.springframework.util.StringUtils.isEmpty;

@RequiredArgsConstructor
@RestController
class UserController {

    private final UserFacade userFacade;

    @PostMapping("/users")
    @PreAuthorize("@security.isOwner(#user.id)")
    public ResponseEntity<?> save(@RequestBody User user) {
        User current = updateUser(user);
        setDefaultPhotoFor(current);
        setIdIfManuallyCreated(current);
        return ResponseEntity.ok(new Resource<>(userFacade.save(user)));
    }


    @PostMapping("/users/{userId}")
    User getUser(@PathVariable String userId) {
        return userFacade.findById(userId);
    }

    @PostMapping("/users/{userId}/participationData")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<?> assignParticipationData(
            @PathVariable String userId,
            @RequestBody ParticipationData participationData) {
        User current = userFacade.findById(userId);
//TODO migrate participation data and implement this logic
//        current.setParticipationData(participationRepository.findById(participationData.getId()));
        return ResponseEntity.ok(new Resource<>(userFacade.save(current)));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("@security.isOwner(#id) || @security.isAdmin()")
    public ResponseEntity<?> getById(@PathVariable String id) {
        User user = userFacade.findById(id);
        return ResponseEntity.ok(new Resource<>(user));
    }

    @GetMapping("/users/{id}/public")
    public ResponseEntity<?> getPublicById(@PathVariable String id) {
        User user = userFacade.findById(id);
        return ResponseEntity.ok(new Resource<>(user.toPublicUser()));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAll() {
        Set<User> users = userFacade.findAll();
        return ResponseEntity.ok(new Resources<>(users));
    }

    @GetMapping("/users/search/admins")
    public ResponseEntity<?> getAdmins() {
        Set<PublicUser> admins = userFacade.findAdmins();
        return ResponseEntity.ok(new Resources<>(admins));
    }

    @GetMapping("/users/search/volunteers")
    public ResponseEntity<?> getVolunteers() {
        Set<PublicUser> volunteers = userFacade.findVolunteers();
        return ResponseEntity.ok(new Resources<>(volunteers));
    }

    @PostMapping("/users/{userId}/volunteer/{isVolunteer}")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> markAsVolunteer(@PathVariable("userId") String userId,
            @PathVariable("isVolunteer") boolean isVolunteer) {
        User user = userFacade.findById(userId);
        user.setVolunteer(isVolunteer);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/search/speakers")
    public ResponseEntity<?> getSpeakers() {
        Set<PublicUser> speakers = userFacade.findAcceptedSpeakers();
        return ResponseEntity.ok(new Resources<>(speakers));
    }

    private void setDefaultPhotoFor(User user) {
        if (isEmpty(user.getPhoto())) {
            Gravatar gravatar = new Gravatar(300, GENERAL_AUDIENCES, BLANK);
            String url = gravatar.getUrl(user.getEmail());
            user.setPhoto(url.replace("http:", "https:"));
        }
    }

    private User updateUser(User user) {
        if (isEmpty(user.getId())) {
            return user;
        } else {
            User current = userFacade.findById(user.getId());
            current.updateFields(user);
            return current;
        }
    }

    private void setIdIfManuallyCreated(User user) {
        if (StringUtils.isEmpty(user.getId())) {
            user.setId(UUID.randomUUID().toString());
        }
    }
}
