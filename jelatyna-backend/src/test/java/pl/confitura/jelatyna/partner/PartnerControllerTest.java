package pl.confitura.jelatyna.partner;

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

class PartnerControllerTest extends BaseIntegrationTest {

    @Autowired
    private PartnerRepository repository;

    @Autowired
    private TransactionTemplate txTemplate;

    private Partner gold;
    private Partner hidden;

    @BeforeEach
    public void setUp() {
        SecurityHelper.asAdmin();
        txTemplate.executeWithoutResult(status ->
                repository.findAll().forEach(p -> repository.deleteById(p.getId())));

        txTemplate.executeWithoutResult(status -> {
            Partner g = new Partner();
            g.setName("XTB");
            g.setType("gold");
            g.setWww("https://xtb.com");
            g.setDescription("A **gold** partner.");
            g.setOrientation("horizontal");
            g.setPublished(true);
            gold = repository.save(g);

            Partner h = new Partner();
            h.setName("Draft Co");
            h.setType("bronze");
            h.setPublished(false);
            hidden = repository.save(h);
        });
    }

    @Test
    void publicListReturnsOnlyPublishedPartners() throws Exception {
        mockMvc.perform(get("/partners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("XTB")))
                .andExpect(jsonPath("$[0].type", is("gold")))
                .andExpect(jsonPath("$[0].orientation", is("horizontal")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminListReturnsAllPartnersIncludingUnpublished() throws Exception {
        mockMvc.perform(get("/partners/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void returnsPartnerById() throws Exception {
        mockMvc.perform(get("/partners/" + gold.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("XTB")));
    }

    @Test
    void returns404WhenPartnerNotFound() throws Exception {
        mockMvc.perform(get("/partners/nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createsPartner() throws Exception {
        String body = """
                {
                  "slug":"dpd",
                  "name":"DPD",
                  "type":"bronze",
                  "www":"https://dpd.com",
                  "description":"Delivery partner.",
                  "orientation":"box",
                  "published":true
                }""";
        mockMvc.perform(post("/partners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.slug", is("dpd")))
                .andExpect(jsonPath("$.name", is("DPD")))
                .andExpect(jsonPath("$.type", is("bronze")))
                .andExpect(jsonPath("$.published", is(true)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatesPartner() throws Exception {
        String body = """
                {
                  "name":"XTB Group",
                  "published":false
                }""";
        mockMvc.perform(put("/partners/" + gold.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("XTB Group")))
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.type", is("gold")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletesPartner() throws Exception {
        mockMvc.perform(delete("/partners/" + gold.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/partners/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
