package pl.confitura.jelatyna.presentation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.Day;
import pl.confitura.jelatyna.agenda.DayRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.rating.RateValue;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PresentationRatingControllerTest extends BaseIntegrationTest {

    @Autowired
    PresentationRepository presentationRepository;
    @Autowired
    DayRepository dayRepository;

    private Presentation presentation;
    private final String reviewerToken = UUID.randomUUID().toString();

    @BeforeEach
    void createPresentation() {
        SecurityHelper.asAdmin();
        // A past-dated agenda day opens the global rating window.
        dayRepository.save(new Day().setId("rate-test-day")
                .setDate(LocalDate.now().minusDays(1)).setLabel("Day").setDisplayOrder(1));
        presentation = presentationRepository.save(new Presentation()
                .setTitle("Talk")
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("easy")
                .setLanguage("pl"));
        SecurityHelper.cleanSecurity();
    }

    @AfterEach
    void cleanUpDay() {
        SecurityHelper.asAdmin();
        if (dayRepository.findById("rate-test-day") != null) {
            dayRepository.deleteById("rate-test-day");
        }
        SecurityHelper.cleanSecurity();
    }

    @Test
    void ratingIsEnabledByDefault() throws Exception {
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/rating-enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratingEnabled").value(true));
    }

    @Test
    void adminCanDisableAndReEnableRating() throws Exception {
        SecurityHelper.asAdmin();
        mockMvc.perform(post("/presentations/" + presentation.getId() + "/rating/disable"))
                .andExpect(status().isOk());
        SecurityHelper.cleanSecurity();

        mockMvc.perform(get("/presentations/" + presentation.getId() + "/rating-enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratingEnabled").value(false));

        SecurityHelper.asAdmin();
        mockMvc.perform(post("/presentations/" + presentation.getId() + "/rating/enable"))
                .andExpect(status().isOk());
        SecurityHelper.cleanSecurity();

        mockMvc.perform(get("/presentations/" + presentation.getId() + "/rating-enabled"))
                .andExpect(jsonPath("$.ratingEnabled").value(true));
    }

    @Test
    void ratingIsRejectedWhenDisabled() throws Exception {
        SecurityHelper.asAdmin();
        mockMvc.perform(post("/presentations/" + presentation.getId() + "/rating/disable"))
                .andExpect(status().isOk());
        SecurityHelper.cleanSecurity();

        mockMvc.perform(post("/presentations/" + presentation.getId() + "/ratings")
                        .content(json(rateRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void ratingStatusIsOpenWhenAgendaDayHasPassed() throws Exception {
        mockMvc.perform(get("/rating/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.open").value(true))
                .andExpect(jsonPath("$.opensAt").value(notNullValue()));
    }
    // The window-closed path (rating rejected before the first agenda day) is covered
    // by RatingWindowServiceTest; forcing a closed window here is unreliable because
    // other test classes leave FK-referenced agenda days behind in the shared DB.

    @Test
    void disablingRatingRequiresAdmin() throws Exception {
        mockMvc.perform(post("/presentations/" + presentation.getId() + "/rating/disable"))
                .andExpect(status().is4xxClientError());
    }

    private RateRequest rateRequest() {
        return new RateRequest()
                .setReviewerToken(reviewerToken)
                .setValue(RateValue.AWESOME.getNumericValue());
    }
}
