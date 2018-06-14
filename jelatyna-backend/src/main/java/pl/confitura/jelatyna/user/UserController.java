package pl.confitura.jelatyna.user;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.StringUtils.isEmpty;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarRating;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

@RepositoryRestController
public class UserController {

    private UserRepository repository;
    private PresentationRepository presentationRepository;
    private Security security;

    @Autowired
    public UserController(UserRepository repository, PresentationRepository presentationRepository, Security security) {
        this.repository = repository;
        this.presentationRepository = presentationRepository;
        this.security = security;
    }

    @PostMapping("/users")
    @PreAuthorize("@security.isOwner(#user.id)")
    public ResponseEntity<?> save(@RequestBody User user) {
        User current = updateUser(user);
        setDefaultPhotoFor(current);
        setIdIfManuallyCreated(current);
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
        return ResponseEntity.ok(new Resource<>(new PublicUser(user)));
    }

    @GetMapping("/users/search/admins")
    public ResponseEntity<?> getAdmins() {
        Set<PublicUser> admins = repository.findAdmins().stream().map(PublicUser::new).collect(Collectors.toSet());
        return ResponseEntity.ok(new Resources<>(admins));
    }

    @GetMapping("/users/search/volunteers")
    public ResponseEntity<?> getVolunteers() {
        Set<PublicUser> volunteers = repository.findVolunteers().stream().map(PublicUser::new).collect(Collectors.toSet());
        return ResponseEntity.ok(new Resources<>(volunteers));
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
        if (isEmpty(presentation.getId()) && !security.isAdmin()) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
        User speaker = repository.findById(userId);
        presentation.setSpeaker(speaker);
        retainStatus(presentation);
        Presentation saved = presentationRepository.save(presentation);
        return ResponseEntity.ok(new Resource<>(saved));
    }

    @GetMapping("/users/search/speakers")
    public ResponseEntity<?> getSpeakers() {
        Set<Resource<?>> speakers = Streams.stream(repository.findAllAccepted())
                .flatMap(row -> {
                    Set<User> users = Sets.newHashSet((User) row[0]);
                    if (row[1] != null) {
                        users.add((User) row[1]);
                    }
                    return users.stream();
                })
                .distinct()
                .map(PublicUser::new)
                .map(speaker -> new Resource<>(speaker))
                .collect(Collectors.toSet());
        return ResponseEntity.ok(new Resources<>(speakers));
    }

    private void retainStatus(Presentation presentation) {
        if (!isEmpty(presentation.getId())) {
            Presentation saved = presentationRepository.findById(presentation.getId());
            presentation.setStatus(saved.getStatus());
        }
    }

    private void setDefaultPhotoFor(@RequestBody User user) {
        if (isEmpty(user.getPhoto())) {
            Gravatar gravatar = new Gravatar(300, GravatarRating.GENERAL_AUDIENCES, GravatarDefaultImage.BLANK);
            String url = gravatar.getUrl(user.getEmail());
            user.setPhoto(url.replace("http:", "https:"));
        }
    }
}
