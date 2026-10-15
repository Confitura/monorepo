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

class FaqCategoryControllerTest extends BaseIntegrationTest {

    @Autowired
    private FaqEntryRepository entryRepository;

    @Autowired
    private FaqCategoryRepository categoryRepository;

    @Autowired
    private TransactionTemplate txTemplate;

    private FaqCategory general;
    private FaqCategory venue;

    @BeforeEach
    public void setUp() {
        SecurityHelper.asAdmin();
        txTemplate.executeWithoutResult(status -> {
            entryRepository.findAllOrdered().forEach(e -> entryRepository.deleteById(e.getId()));
            categoryRepository.findAllByOrderByDisplayOrderAsc()
                    .forEach(c -> categoryRepository.deleteById(c.getId()));
        });
        txTemplate.executeWithoutResult(status -> {
            general = categoryRepository.save(
                    new FaqCategory().setName("General").setDisplayOrder(0).setPublished(true));
            venue = categoryRepository.save(
                    new FaqCategory().setName("Venue").setDisplayOrder(1).setPublished(true));
            entryRepository.save(new FaqEntry()
                    .setCategory(general).setQuestion("Q1").setAnswer("A1").setDisplayOrder(0).setPublished(true));
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listsCategoriesInDisplayOrder() throws Exception {
        mockMvc.perform(get("/faq-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("General")))
                .andExpect(jsonPath("$[1].name", is("Venue")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRejectsDuplicateNameCaseInsensitively() throws Exception {
        mockMvc.perform(post("/faq-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" general \"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void renameRejectsCollisionWithAnotherCategory() throws Exception {
        mockMvc.perform(put("/faq-categories/" + general.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Venue\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void togglesVisibility() throws Exception {
        mockMvc.perform(put("/faq-categories/" + general.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"published\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.name", is("General")));
    }

    @Test
    void hiddenCategoryEntriesAreExcludedFromPublicList() throws Exception {
        // sanity: entry visible while its category is published
        mockMvc.perform(get("/faq-entries")).andExpect(jsonPath("$", hasSize(1)));

        SecurityHelper.asAdmin();
        txTemplate.executeWithoutResult(status ->
                categoryRepository.save(categoryRepository.findById(general.getId()).setPublished(false)));

        mockMvc.perform(get("/faq-entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reordersCategories() throws Exception {
        String body = "{\"ids\":[\"" + venue.getId() + "\",\"" + general.getId() + "\"]}";
        mockMvc.perform(put("/faq-categories/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/faq-categories"))
                .andExpect(jsonPath("$[0].name", is("Venue")))
                .andExpect(jsonPath("$[1].name", is("General")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void mergesCategoriesMovingEntriesAndDeletingSource() throws Exception {
        String body = "{\"from\":\"" + general.getId() + "\",\"to\":\"" + venue.getId() + "\"}";
        mockMvc.perform(post("/faq-categories/merge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/faq-categories"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Venue")));
        mockMvc.perform(get("/faq-entries/all"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Venue")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRefusesNonEmptyCategoryButAllowsEmpty() throws Exception {
        mockMvc.perform(delete("/faq-categories/" + general.getId()))
                .andExpect(status().isConflict());

        mockMvc.perform(delete("/faq-categories/" + venue.getId()))
                .andExpect(status().isNoContent());
    }
}
