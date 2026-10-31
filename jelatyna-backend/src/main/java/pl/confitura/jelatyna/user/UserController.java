package pl.confitura.jelatyna.user;

import static com.timgroup.jgravatar.GravatarDefaultImage.BLANK;
import static com.timgroup.jgravatar.GravatarRating.GENERAL_AUDIENCES;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.timgroup.jgravatar.Gravatar;
import lombok.RequiredArgsConstructor;
import pl.confitura.jelatyna.conference.registration.web.UserWithTicket;
import pl.confitura.jelatyna.identity.user.domain.Speakers;
import pl.confitura.jelatyna.platform.security.JelatynaPrincipal;
import pl.confitura.jelatyna.platform.storage.ResourceStorage;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationRepository;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repository;
    private final ParticipationRepository participationRepository;
    private final Speakers speakers;
    private final ResourceStorage storage;

    @PostMapping("/users")
    @PreAuthorize("@security.isOwner(#user.id)")
    // Documented as the API's User, which shows the ticket: the entity alone would replace that schema.
    public ResponseEntity<?> save(@RequestBody @Schema(implementation = UserWithTicket.class) User user) {
        User current = updateUser(user);
        setDefaultPhotoFor(current);
        setIdIfManuallyCreated(current);
        return ResponseEntity.ok(withTicket(repository.save(current)));
    }

    @PostMapping("/resources/{userId}")
    @Operation(
            summary = "Store speaker photo",
            description = "Upload a Multipart file for the speaker's photo",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "File to upload",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schemaProperties = @SchemaProperty(
                                    name = "file",
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    )
            ),
            parameters = @Parameter(name = "userId", description = "ID of the user", required = true)
    )
    @Transactional
    @PreAuthorize("@security.isOwner(#userId)")
    public String storeUserProfilePicture(@RequestParam("file") MultipartFile file, @PathVariable String userId) throws IOException {
        User user = repository.findById(userId);
        String path = storage.store(user.getId(), file, "photos");
        user.setPhoto(path);
        repository.save(user);
        return user.getPhoto();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("@security.isOwner(#id) || @security.isAdmin()")
    public ResponseEntity<UserWithTicket> getById(@PathVariable String id) {
        User user = repository.findById(id);
        return ResponseEntity.ok(user == null ? null : withTicket(user));
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
