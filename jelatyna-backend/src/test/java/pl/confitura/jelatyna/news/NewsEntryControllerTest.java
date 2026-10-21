package pl.confitura.jelatyna.news;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.support.TransactionTemplate;
import pl.confitura.jelatyna.BaseIntegrationTest;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

class NewsEntryControllerTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate txTemplate;

    private NewsEntry tickets;
    private NewsEntry draft;

    @BeforeEach
    void entries() {
        txTemplate.executeWithoutResult(status ->
                entityManager.createQuery("delete from NewsEntry").executeUpdate());
        tickets = persist("Tickets", "tickets", "2025-09-12T08:11:12Z", true);
        draft = persist("Draft", "draft", "2026-10-01T10:00:00Z", false);
        persist("Scheduled", "scheduled", "2099-01-01T00:00:00Z", true);
    }

    @Test
    void listsEveryEntryNewestFirst() throws Exception {
        mockMvc.perform(get("/news-entries").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", contains("Scheduled", "Draft", "Tickets")))
                .andExpect(jsonPath("$[1].id", is(draft.getId())))
                .andExpect(jsonPath("$[1].slug", is("draft")))
                .andExpect(jsonPath("$[1].body", is("<p>Draft</p>")))
                .andExpect(jsonPath("$[1].date", is("2026-10-01T10:00:00Z")))
                .andExpect(jsonPath("$[1].published", is(false)));
    }

    @Test
    void getsOneEntry() throws Exception {
        mockMvc.perform(get("/news-entries/" + tickets.getId()).with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Tickets")))
                .andExpect(jsonPath("$.slug", is("tickets")));
        mockMvc.perform(get("/news-entries/does-not-exist").with(admin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsAnEntrySluggedFromItsTitle() throws Exception {
        Instant date = Instant.parse("2026-10-21T10:15:30Z");
        create(new NewsEntryRequest("Agenda is out", null, "**Read** it", date, true))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is("Agenda is out")))
                .andExpect(jsonPath("$.slug", is("agenda-is-out")))
                .andExpect(jsonPath("$.body", is("**Read** it")))
                .andExpect(jsonPath("$.date", is("2026-10-21T10:15:30Z")))
                .andExpect(jsonPath("$.published", is(true)));

        mockMvc.perform(get("/news-entries").with(admin()))
                .andExpect(jsonPath("$[*].slug", contains("scheduled", "agenda-is-out", "draft", "tickets")));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            We’re in the home stretch! | were-in-the-home-stretch
            agenda&tickets             | agenda-tickets
            Don't miss Confitura 2026  | dont-miss-confitura-2026
            Zażółć gęślą jaźń          | zazolc-gesla-jazn
            "  -- Łódź, KRAKÓW --  "   | lodz-krakow
            """)
    void slugsTheTitleByTheSlugRule(String title, String slug) throws Exception {
        create(titled(title, null)).andExpect(jsonPath("$.slug", is(slug)));
    }

    @Test
    void aGeneratedSlugInUseGetsTheNextFreeNumber() throws Exception {
        create(titled("TICKETS", null)).andExpect(jsonPath("$.slug", is("tickets-2")));
        create(titled("Tickets!", null)).andExpect(jsonPath("$.slug", is("tickets-3")));
    }

    @Test
    void keepsAGivenSlugUnlessAnotherEntryHasItOrItIsNoSlug() throws Exception {
        create(titled("Tickets", "tickets-2026"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug", is("tickets-2026")));
        create(titled("Tickets", "draft")).andExpect(status().isConflict());
        create(titled("Tickets", "Not a slug")).andExpect(status().isBadRequest());
        create(titled("🎉", null)).andExpect(status().isBadRequest());
    }

    @Test
    void refusesAnEntryWithoutTitleBodyOrDate() throws Exception {
        Instant date = Instant.parse("2026-10-21T10:00:00Z");
        create(new NewsEntryRequest(null, "no-title", "Body", date, true)).andExpect(status().isBadRequest());
        create(new NewsEntryRequest("No body", null, null, date, true)).andExpect(status().isBadRequest());
        create(new NewsEntryRequest("No date", null, "Body", null, true)).andExpect(status().isBadRequest());
    }

    @Test
    void deletesAnEntry() throws Exception {
        mockMvc.perform(delete("/news-entries/" + draft.getId()).with(admin()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/news-entries").with(admin()))
                .andExpect(jsonPath("$[*].title", contains("Scheduled", "Tickets")));
    }

    @Test
    void refusesEveryoneButAdmins() throws Exception {
        RequestPostProcessor anonymous = request -> request;
        String body = json(titled("Defaced", "defaced"));
        String draftUrl = "/news-entries/" + draft.getId();
        for (RequestPostProcessor nonAdmin : List.of(anonymous, user("not-an-admin"))) {
            mockMvc.perform(get("/news-entries").with(nonAdmin)).andExpect(status().isForbidden());
            mockMvc.perform(get(draftUrl).with(nonAdmin)).andExpect(status().isForbidden());
            mockMvc.perform(post("/news-entries").with(nonAdmin).contentType(APPLICATION_JSON).content(body))
                    .andExpect(status().isForbidden());
            mockMvc.perform(delete(draftUrl).with(nonAdmin)).andExpect(status().isForbidden());
        }

        mockMvc.perform(get("/news-entries").with(admin()))
                .andExpect(jsonPath("$[*].title", contains("Scheduled", "Draft", "Tickets")));
    }

    private static NewsEntryRequest titled(String title, String slug) {
        return new NewsEntryRequest(title, slug, "Body", Instant.parse("2026-10-21T10:00:00Z"), true);
    }

    private ResultActions create(NewsEntryRequest request) throws Exception {
        return mockMvc.perform(post("/news-entries").with(admin())
                .contentType(APPLICATION_JSON).content(json(request)));
    }

    private NewsEntry persist(String title, String slug, String date, boolean published) {
        NewsEntry entry = new NewsEntry();
        entry.setTitle(title);
        entry.setSlug(slug);
        entry.setBody("<p>" + title + "</p>");
        entry.setDate(Instant.parse(date));
        entry.setPublished(published);
        txTemplate.executeWithoutResult(status -> entityManager.persist(entry));
        return entry;
    }
}
