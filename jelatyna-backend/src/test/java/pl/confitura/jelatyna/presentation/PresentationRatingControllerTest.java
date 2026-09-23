package pl.confitura.jelatyna.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.rating.RateValue;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PresentationRatingControllerTest extends BaseIntegrationTest {

    @Autowired
    PresentationRepository presentationRepository;

    private Presentation presentation;
    private final String reviewerToken = UUID.randomUUID().toString();

    @BeforeEach
    void createPresentation() {
        SecurityHelper.asAdmin();
        presentation = presentationRepository.save(new Presentation()
                .setTitle("Talk")
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("easy")
                .setLanguage("pl"));
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

    // Note: the "enabled -> 201" happy path is not asserted here because the
    // underlying rating persistence uses a `value` column that is a reserved word
    // in H2 (the same reason RatingApiTest is @Disabled). The guard added by this
    // change runs before that query, so `ratingIsRejectedWhenDisabled` still covers it.

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
