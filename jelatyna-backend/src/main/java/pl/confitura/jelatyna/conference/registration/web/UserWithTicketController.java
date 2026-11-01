package pl.confitura.jelatyna.conference.registration.web;

import static com.timgroup.jgravatar.GravatarDefaultImage.BLANK;
import static com.timgroup.jgravatar.GravatarRating.GENERAL_AUDIENCES;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.StringUtils.hasText;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.timgroup.jgravatar.Gravatar;
import lombok.RequiredArgsConstructor;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationRepository;
import pl.confitura.jelatyna.identity.user.domain.Speakers;
import pl.confitura.jelatyna.platform.security.JelatynaPrincipal;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

/** Serves a User's profile with their ticket, so the user package needn't know registration. */
// The tag these endpoints had while UserController served them, so openapi.json stays the same.
@Tag(name = "user-controller")
@RestController
@RequiredArgsConstructor
public class UserWithTicketController {

    private final UserRepository repository;
    private final ParticipationRepository participationRepository;
    private final Speakers speakers;

    @PostMapping("/users")
    @PreAuthorize("@security.isOwner(#user.id)")
    // Documented as the API's User, which shows the ticket: the entity alone would replace that schema.
    public ResponseEntity<?> save(@RequestBody @Schema(implementation = UserWithTicket.class) User user) {
        User current = updateUser(user);
        setDefaultPhotoFor(current);
        setIdIfManuallyCreated(current);
        return ResponseEntity.ok(withTicket(repository.save(current)));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("@security.isOwner(#id) || @security.isAdmin()")
    public ResponseEntity<UserWithTicket> getById(@PathVariable String id) {
        User user = repository.findById(id);
        return ResponseEntity.ok(user == null ? null : withTicket(user));
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserWithTicket> getCurrentUser(@AuthenticationPrincipal JelatynaPrincipal user) {
        if (user == null) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(withTicket(repository.findById(user.getId())));
    }

    // User holds only its ticket's id; registration has the ticket, and presentation knows whether the User speaks.
    private UserWithTicket withTicket(User user) {
        String ticketId = user.getParticipationDataId();
        return new UserWithTicket(user, ticketId == null ? null : participationRepository.findById(ticketId),
                speakers.isSpeaker(user.getId()));
    }

    private void setDefaultPhotoFor(User user) {
        if (!hasText(user.getPhoto())) {
            Gravatar gravatar = new Gravatar(300, GENERAL_AUDIENCES, BLANK);
            String url = gravatar.getUrl(user.getEmail());
            user.setPhoto(url.replace("http:", "https:"));
        }
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
