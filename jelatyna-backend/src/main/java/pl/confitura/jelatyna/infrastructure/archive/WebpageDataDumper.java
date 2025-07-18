package pl.confitura.jelatyna.infrastructure.archive;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import pl.confitura.jelatyna.user.UserController;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
public class WebpageDataDumper {

    private final ObjectMapper objectMapper;
    private final UserController userController;
    private final String targetDirectory;

    @SneakyThrows
    @Scheduled(fixedRate = 60000)
    public void dumpAdmins() {
        Path adminsPath = Path.of(targetDirectory, "/users/search/admins.json");
        log.info("Dumping admins from webpage data to {}", adminsPath);
        if (Files.notExists(adminsPath)) {
            Files.createDirectories(adminsPath.getParent());
            Files.createFile(adminsPath);
        }
        var admins = userController.getAdmins().getBody();
        String json = objectMapper.writeValueAsString(admins);
        Files.writeString(adminsPath, json);
    }
}
