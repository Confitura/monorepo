package pl.confitura.jelatyna.platform.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.google.common.io.Files.*;

@Service
@RequiredArgsConstructor
public class ResourceStorage {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "webp", "svg");

    private final ResourceConfigurationProperties properties;

    public String store(String fileName, MultipartFile file, String... paths) throws IOException {
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
