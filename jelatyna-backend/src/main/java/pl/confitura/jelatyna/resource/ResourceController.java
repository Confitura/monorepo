package pl.confitura.jelatyna.resource;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    private ResourceStorage storage;

    @Autowired
    public ResourceController(ResourceStorage storage) {
        this.storage = storage;
    }

    @PostMapping("/{userId}")
    public void store(@RequestParam MultipartFile file, @PathVariable String userId) throws IOException {
        storage.storeSpeaker(file, userId);
    }

    @PostMapping("/partners/{id}")
    public void storePartnerLogo(@RequestParam MultipartFile file, @PathVariable String id) throws IOException {
        storage.storePartnerLogo(file, id);
    }

}
