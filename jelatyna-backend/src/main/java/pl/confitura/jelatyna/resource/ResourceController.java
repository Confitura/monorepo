package pl.confitura.jelatyna.resource;

import java.io.IOException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import pl.confitura.jelatyna.news.NewsEntry;
import pl.confitura.jelatyna.news.NewsEntryRepository;
import pl.confitura.jelatyna.partner.Partner;
import pl.confitura.jelatyna.partner.PartnerRepository;
import pl.confitura.jelatyna.platform.storage.ResourceStorage;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceStorage storage;
    private final UserRepository repository;
    private final PartnerRepository partnerRepository;
    private final NewsEntryRepository newsEntryRepository;


    @PostMapping("/{userId}")
    @Operation(
            summary = "Store speaker photo",
            description = "Upload a Multipart file for the speaker's photo",
            requestBody = @RequestBody(
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

    @PostMapping("/partners/{id}")
    @Operation(
            summary = "Store partner logo",
            description = "Upload a Multipart file for the partner's logo",
            requestBody = @RequestBody(
                    description = "File to upload",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schemaProperties = @SchemaProperty(
                                    name = "file",
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    )
            ),
            parameters = @Parameter(name = "id", description = "ID of the partner", required = true)
    )
    @Transactional
    @PreAuthorize("@security.isAdmin()")
    public void storePartnerLogo(@RequestParam("file") MultipartFile file, @PathVariable String id) throws IOException {
        Partner partner = partnerRepository.findById(id);
        partner.setLogo(storage.store(id, file, "photos"));
        partnerRepository.save(partner);
    }

    @PostMapping("/news-entries/{id}")
    @Operation(
            summary = "Store news entry cover",
            description = "Upload a Multipart file for the news entry's cover image, replacing any it had",
            requestBody = @RequestBody(
                    description = "File to upload",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schemaProperties = @SchemaProperty(
                                    name = "file",
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    )
            ),
            parameters = @Parameter(name = "id", description = "ID of the news entry", required = true)
    )
    @Transactional
    @PreAuthorize("@security.isAdmin()")
    public void storeNewsEntryCover(@RequestParam("file") MultipartFile file, @PathVariable String id) throws IOException {
        NewsEntry entry = newsEntryRepository.findById(id);
        // Refused before storing: the folder also holds speaker photos and partner logos named by id.
        if (entry == null) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        entry.setCover(storage.store(id, file, "photos"));
        newsEntryRepository.save(entry);
    }

}
