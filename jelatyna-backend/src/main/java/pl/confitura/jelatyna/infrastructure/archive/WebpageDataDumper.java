package pl.confitura.jelatyna.infrastructure.archive;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import pl.confitura.jelatyna.page.PageController;
import pl.confitura.jelatyna.user.UserController;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
public class WebpageDataDumper {

    private final ObjectMapper objectMapper;
    private final String targetDirectory;

    private final UserController userController;
    private final PageController pageController;

    @Scheduled(fixedRate = 60000)
    public void dumpAll() {
        dumpAdmins();
        dumpPages();
    }

    void dumpPages() {
        for (String page : pageController.getPages()) {
            var content = pageController.getPage(page);
            dumbData(content.getBody(), "/pages/" + page + ".json");
        }
    }

    public void dumpAdmins() {
        var admins = userController.getAdmins().getBody();
        dumbData(admins, "/users/search/admins.json");
    }

    @SneakyThrows
    private void dumbData(Object data, String targetPath) {
        String json = objectMapper.writeValueAsString(data);
        dumpData(json, targetPath);
    }

    @SneakyThrows
    private void dumpData(String json, String targetPath) {
        Path filePath = Path.of(targetDirectory, targetPath);
        log.info("Dumping data to {}", filePath);
        if (Files.notExists(filePath)) {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
        }
        Files.writeString(filePath, json);
    }
}
