package pl.confitura.jelatyna.presentation.rating;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.Day;
import pl.confitura.jelatyna.agenda.DayRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.presentation.RateRequest;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RatePersistenceTest extends BaseIntegrationTest {

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
        dayRepository.deleteById("rate-test-day");
        SecurityHelper.cleanSecurity();
    }

    @Test
    void ratingCanBeCreatedAndUpserted() throws Exception {
        // Reaching 201 proves the reserved-word `value` column now maps on H2: both the
        // upsert lookup (SELECT ... "value" ...) and the INSERT run without a SQL syntax error.

        // insert
        rate(RateValue.AWESOME).andExpect(status().isCreated());

        // same reviewer rates again — upsert path (findByReviewerTokenAndPresentationId + update)
        rate(RateValue.GREAT).andExpect(status().isCreated());
    }

    private ResultActions rate(RateValue value) throws Exception {
        RateRequest request = new RateRequest()
                .setReviewerToken(reviewerToken)
                .setValue(value.getNumericValue());
        return mockMvc.perform(post("/presentations/" + presentation.getId() + "/ratings")
                .content(json(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
}
