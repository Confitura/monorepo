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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void latestIsNullWhenNoEntryIsPublic() throws Exception {
        mockMvc.perform(get("/published/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latest").value(nullValue()))
                .andExpect(jsonPath("$.all", hasSize(0)));
    }

    @Test
    void listsOnlyPublishedEntriesWhoseDateHasPassedNewestFirst() throws Exception {
        persist("Older", "older", Instant.parse("2025-08-20T19:12:42.265697Z"), true);
        persist("Newest", "newest", Instant.parse("2026-09-27T10:27:57Z"), true, COVER);
        persist("Middle", "middle", Instant.parse("2026-01-29T17:17:46Z"), true);

        mockMvc.perform(get("/published/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.all[*].title", contains("Newest", "Middle", "Older")))
                .andExpect(jsonPath("$.all[2].publishedAt", is("2025-08-20T19:12:42.265697Z")))
                .andExpect(jsonPath("$.all[1]", hasEntry("cover", null)))
                .andExpect(jsonPath("$.latest.title", is("Newest")))
                .andExpect(jsonPath("$.latest.slug", is("newest")))
                .andExpect(jsonPath("$.latest.body", is("<p>Newest</p>")))
                .andExpect(jsonPath("$.latest.publishedAt", is("2026-09-27T10:27:57Z")))
                .andExpect(jsonPath("$.latest.cover", is(COVER)));
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
