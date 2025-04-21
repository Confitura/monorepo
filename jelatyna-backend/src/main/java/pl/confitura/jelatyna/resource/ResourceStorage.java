package pl.confitura.jelatyna.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pl.confitura.jelatyna.partner.Partner;
import pl.confitura.jelatyna.partner.PartnerRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import static com.google.common.io.Files.*;

@Service
@RequiredArgsConstructor
public class ResourceStorage {
    @Value("${resources.path}")
    private String rootPath;
    @Value("${resources.folder}")
    private String folder;
    @Value("${resources.resources-base-url}")
    private String resourcesBaseUrl;

    private final UserRepository repository;
    private final PartnerRepository partnerRepository;

    @Transactional
    @PreAuthorize("@security.isOwner(#userId)")
    String storeSpeaker(MultipartFile file, String userId) throws IOException {
        User user = repository.findById(userId);
        String path = doStore(user.getId(), file, "photos");
        user.setPhoto(path);
        repository.save(user);
        return user.getPhoto();
    }

    @Transactional
    @PreAuthorize("@security.isAdmin()")
    void storePartnerLogo(MultipartFile file, String id) throws IOException {
        Partner partner = partnerRepository.findById(id);
        partner.setLogo(doStore(id, file, "photos"));
        partnerRepository.save(partner);
    }

    private String doStore(String fileName, MultipartFile file, String... paths) throws IOException {
        Path path = Files.createDirectories(Paths.get(folder, paths));
        Path filePath = Paths.get(path.toString(), fileName + "." + getFileExtension(file.getOriginalFilename()));
        Files.write(filePath, file.getBytes());
        return resourcesBaseUrl + "/" + rootPath  + filePath.toString().replace(folder, "");
    }
}
