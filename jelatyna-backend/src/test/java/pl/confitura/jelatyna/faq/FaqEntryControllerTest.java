package pl.confitura.jelatyna.faq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.support.TransactionTemplate;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FaqEntryControllerTest extends BaseIntegrationTest {

    @Autowired
    private FaqEntryRepository repository;

    @Autowired
    private TransactionTemplate txTemplate;

    private FaqEntry first;
    private FaqEntry second;
    private FaqEntry hidden;

    @BeforeEach
    public void setUp() {
        SecurityHelper.asAdmin();
        txTemplate.executeWithoutResult(status ->
                repository.findAllOrdered().forEach(e -> repository.deleteById(e.getId())));

        txTemplate.executeWithoutResult(status -> {
            first = repository.save(new FaqEntry()
                    .setCategory("General").setQuestion("First").setAnswer("A1")
                    .setDisplayOrder(0).setPublished(true));
            second = repository.save(new FaqEntry()
                    .setCategory("General").setQuestion("Second").setAnswer("A2")
                    .setDisplayOrder(1).setPublished(true));
            hidden = repository.save(new FaqEntry()
                    .setCategory("General").setQuestion("Hidden").setAnswer("A3")
                    .setDisplayOrder(2).setPublished(false));
        });
    }

    @Test
    void publicListReturnsOnlyPublishedEntriesInOrder() throws Exception {
        mockMvc.perform(get("/faq-entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].question", is("First")))
                .andExpect(jsonPath("$[1].question", is("Second")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminListReturnsAllEntriesIncludingUnpublished() throws Exception {
        mockMvc.perform(get("/faq-entries/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void returnsEntryById() throws Exception {
        mockMvc.perform(get("/faq-entries/" + first.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question", is("First")))
                .andExpect(jsonPath("$.category", is("General")));
    }

    @Test
    void returns404WhenEntryNotFound() throws Exception {
        mockMvc.perform(get("/faq-entries/does-not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createsEntry() throws Exception {
        String body = """
                {
                  "category":"Venue",
                  "question":"Where is it?",
                  "answer":"At the venue.",
                  "displayOrder":0,
                  "published":true
                }""";
        mockMvc.perform(post("/faq-entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.question", is("Where is it?")))
                .andExpect(jsonPath("$.published", is(true)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatesEntry() throws Exception {
        String body = """
                {
                  "question":"Updated question",
                  "published":false
                }""";
        mockMvc.perform(put("/faq-entries/" + first.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question", is("Updated question")))
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.category", is("General")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletesEntry() throws Exception {
        mockMvc.perform(delete("/faq-entries/" + first.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/faq-entries/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reordersEntries() throws Exception {
        String body = "{\"ids\":[\"" + second.getId() + "\",\"" + first.getId() + "\"]}";
        mockMvc.perform(put("/faq-entries/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        // second now has displayOrder 0, so it sorts before first within the category
        mockMvc.perform(get("/faq-entries/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question", is("Second")))
                .andExpect(jsonPath("$[1].question", is("First")));
    }
}
