package pl.confitura.jelatyna.conference.dashboard.web;

import org.junit.jupiter.api.Test;
import pl.confitura.jelatyna.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.identity.user.web.SecurityHelper.admin;

class NewsletterStatsTest extends BaseIntegrationTest {

    @Test
    void newsletterStatsShowTheSubscriberCountTheNewsletterReports() throws Exception {
        mockMvc.perform(get("/dashboard/newsletter").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscribersCount").value(42));
    }
}
