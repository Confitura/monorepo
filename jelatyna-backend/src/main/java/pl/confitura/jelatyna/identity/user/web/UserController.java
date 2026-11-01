package pl.confitura.jelatyna.identity.user.web;

import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pl.confitura.jelatyna.identity.user.domain.PublicProfile;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;
import pl.confitura.jelatyna.platform.storage.ResourceStorage;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repository;
    private final ResourceStorage storage;

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

}
