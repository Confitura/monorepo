package pl.confitura.jelatyna.infrastructure.archive;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.api.model.InlinePresentationWithSpeakers;
import pl.confitura.jelatyna.news.NewsletterApi;
import pl.confitura.jelatyna.page.PageController;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.UserController;
import pl.confitura.jelatyna.user.PublicProfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class WebpageDataDumper {

    private final ObjectMapper objectMapper;
    private final String targetDirectory;

    private final UserController userController;
    private final PageController pageController;
    private final NewsletterApi newsletterApi;
    private final PresentationRepository presentationRepository;

    private final AtomicReference<Instant> lastDumpAt = new AtomicReference<>();

    @Scheduled(fixedRate = 600000)
    @Transactional
    public void dumpAll() {
        dumpAdmins();
        dumpSpeakers();
        dumpAcceptedPresentations();
        dumpPages();
        dumpNews();
        lastDumpAt.set(Instant.now());
    }

    public Instant getLastDumpAt() {
        return lastDumpAt.get();
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

    @Transactional
    public void dumpSpeakers() {
        var speakers = userController.getSpeakers().getBody();
        dumpEachSpeaker(speakers);
        dumbData(speakers, "/users/search/speakers.json");
    }

    public void dumpEachSpeaker(Collection<PublicProfile> speakers) {
        if (speakers == null) {
            return;
        }
        for (PublicProfile speaker : speakers) {
            dumbData(speaker, "/users/" + speaker.getId() + "/public.json");
        }
    }

    public void dumpAcceptedPresentations() {
        Iterable<Presentation> accepted = presentationRepository.findAccepted();
        List<InlinePresentationWithSpeakers> presentations = StreamSupport.stream(accepted.spliterator(), false)
                .filter(p -> !p.isWorkshop())
                .map(InlinePresentationWithSpeakers::new)
                .collect(toList());
        dumbData(presentations, "/presentations/accepted.json");
    }

    private void dumpNews() {
        try {
            dumbData(DumpedNews.from(newsletterApi.getWebpageNews()), "news.json");
        } catch (IOException e) {
            log.warn("Couldn't dump news", e);
        }
    }

    @SneakyThrows
    private void dumbData(Object data, String targetPath) {
        String json = objectMapper.writeValueAsString(data);
        dumpData(json, targetPath);
    }


    @SneakyThrows
    private void dumpData(String json, String targetPath) {
        Path filePath = Path.of(targetDirectory, targetPath);
        if (Files.notExists(filePath)) {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
        }
        Files.writeString(filePath, json);
    }
}
