package pl.confitura.jelatyna.resource;

import java.io.IOException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceStorage storage;


    @PostMapping("/{userId}")
    @Operation(
            summary = "Store speaker photo",
            description = "Upload a Multipart file for the speaker's photo",
            requestBody = @RequestBody(
                    description = "File to upload",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = MultipartFile.class)
                    )
            ),
            parameters = @Parameter(name = "userId", description = "ID of the user", required = true)
    )
    public String storeUserProfilePicture(@RequestParam("file") MultipartFile file, @PathVariable String userId) throws IOException {
        return storage.storeSpeaker(file, userId);
    }

    @PostMapping("/partners/{id}")
    @Operation(
            summary = "Store partner logo",
            description = "Upload a Multipart file for the partner's logo",
            requestBody = @RequestBody(
                    description = "File to upload",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = MultipartFile.class)
                    )
            ),
            parameters = @Parameter(name = "id", description = "ID of the partner", required = true)
    )
    public void storePartnerLogo(@RequestParam("file") MultipartFile file, @PathVariable String id) throws IOException {
        storage.storePartnerLogo(file, id);
    }

}
