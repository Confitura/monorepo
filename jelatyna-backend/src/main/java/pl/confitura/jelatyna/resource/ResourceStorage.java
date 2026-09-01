package pl.confitura.jelatyna.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
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

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "webp", "svg");

    private final ResourceConfigurationProperties properties;
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
        Path baseDir = Files.createDirectories(Paths.get(properties.folder(), paths)).normalize();
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Unsupported file extension: " + extension);
        }
        Path filePath = baseDir.resolve(fileName + "." + extension).normalize();
        if (!filePath.startsWith(baseDir)) {
            throw new IllegalArgumentException("Resolved path escapes the storage directory");
        }
        Files.write(filePath, file.getBytes());
        return properties.resourcesBaseUrl() + "/" + properties.path() + filePath.toString().replace(properties.folder(), "");
    }

}
