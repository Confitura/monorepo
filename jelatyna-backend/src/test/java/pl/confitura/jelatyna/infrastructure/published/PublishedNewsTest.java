package pl.confitura.jelatyna.infrastructure.published;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.news.NewsEntry;
import pl.confitura.jelatyna.resource.ResourceConfigurationProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PublishedNewsTest extends BaseIntegrationTest {

    private static final String COVER = "https://api.confitura.pl/api/resources/photos/newest.png";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private WebpageDataDumper webpageDataDumper;

    @Autowired
    private ResourceConfigurationProperties resources;

    @BeforeEach
    void onlyEntriesThatAreNotPublic() {
        txTemplate.executeWithoutResult(status ->
                entityManager.createQuery("delete from NewsEntry").executeUpdate());
        // Both are newer than any public entry below, so either would be the latest if it leaked.
        persist("Draft", "draft", Instant.parse("2026-10-01T10:00:00Z"), false);
        persist("Scheduled", "scheduled", Instant.now().plus(1, ChronoUnit.DAYS), true);
    }

    @Test
    void newsJsonInTheWebpageDumpIsBuiltFromPublicEntries() throws Exception {
        persist("Newest", "newest", Instant.parse("2026-09-27T10:27:57Z"), true, COVER);

        webpageDataDumper.dumpAll();

        var newest = new DumpedNews.News("Newest", "newest", "<p>Newest</p>", Instant.parse("2026-09-27T10:27:57Z"), COVER);
        String dumped = Files.readString(Path.of(resources.folder(), "edition-2026", "news.json"));
        assertThat(fromJson(dumped, DumpedNews.class)).isEqualTo(new DumpedNews(newest, List.of(newest)));
    }

    private void persist(String title, String slug, Instant date, boolean published) {
        persist(title, slug, date, published, null);
    }

    private void persist(String title, String slug, Instant date, boolean published, String cover) {
        NewsEntry entry = new NewsEntry();
        entry.setTitle(title);
        entry.setSlug(slug);
        entry.setBody("<p>" + title + "</p>");
        entry.setDate(date);
        entry.setPublished(published);
        entry.setCover(cover);
        txTemplate.executeWithoutResult(status -> entityManager.persist(entry));
    }
}
