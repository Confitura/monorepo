package pl.confitura.jelatyna.resource;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    private ResourceStorage storage;

    @Autowired
    public ResourceController(ResourceStorage storage) {
        this.storage = storage;
    }

    @PostMapping
    public void store(@RequestParam MultipartFile file, @AuthenticationPrincipal JelatynaPrincipal principal) throws IOException {
        storage.storeSpeaker(file, principal);

    }

}
