package pl.confitura.jelatyna.presentation;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.Day;
import pl.confitura.jelatyna.agenda.DayRepository;
import pl.confitura.jelatyna.edition.Edition;
import pl.confitura.jelatyna.edition.EditionRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.rating.Rate;
import pl.confitura.jelatyna.presentation.rating.RateValue;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PresentationRatingControllerTest extends BaseIntegrationTest {

    @Autowired
    PresentationRepository presentationRepository;
    @Autowired
    DayRepository dayRepository;
    @Autowired
    EditionRepository editionRepository;

    private Presentation presentation;
    private final String reviewerToken = UUID.randomUUID().toString();

    @BeforeEach
    void createPresentation() {
        SecurityHelper.asAdmin();
        // A past-dated agenda day opens the global rating window.
        dayRepository.save(new Day().setId("rate-test-day")
                .setDate(LocalDate.now().minusDays(1)).setLabel("Day").setDisplayOrder(1).setEdition(editionRepository.findCurrent()));
        presentation = presentationRepository.save(new Presentation()
                .setTitle("Talk")
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("easy")
                .setLanguage("pl")
                .setEdition(editionRepository.findCurrent()));
        SecurityHelper.cleanSecurity();
    }

    @AfterEach
    void cleanUpDay() {
        SecurityHelper.asAdmin();
        Day day = dayRepository.findInCurrentEditionById("rate-test-day");
        if (day != null) {
            dayRepository.delete(day);
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
    @Transactional
    void ratingACurrentEditionTalkIsAccepted() throws Exception {
        mockMvc.perform(post("/presentations/" + presentation.getId() + "/ratings")
                        .content(json(rateRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void theRatingResponseHasNoReviewerTokenNorTheTalksOtherRatings() throws Exception {
        String otherReviewerToken = UUID.randomUUID().toString();
        rate(rateRequest().setReviewerToken(otherReviewerToken));

        String body = mockMvc.perform(post("/presentations/" + presentation.getId() + "/ratings")
                        .content(json(rateRequest().setComment("Great talk")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.value").value("AWESOME"))
                .andExpect(jsonPath("$.comment").value("Great talk"))
                .andExpect(jsonPath("$.reviewerToken").doesNotExist())
                .andExpect(jsonPath("$.presentation").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        assertThat(body).doesNotContain(reviewerToken, otherReviewerToken);
    }

    @Test
    @Transactional
    void ratingATalkOutsideTheCurrentEditionIsRefused() throws Exception {
        SecurityHelper.asAdmin();
        Edition pastEdition = editionRepository.save(new Edition()
                .setYear(2025)
                .setName("Confitura 2025")
                .setConferenceStart(Instant.parse("2025-06-28T07:00:00Z"))
                .setLastDay(LocalDate.of(2025, 6, 28)));
        Presentation pastTalk = presentationRepository.save(new Presentation()
                .setTitle("Last year's talk")
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("easy")
                .setLanguage("pl")
                .setEdition(pastEdition));
        SecurityHelper.cleanSecurity();

        mockMvc.perform(post("/presentations/" + pastTalk.getId() + "/ratings")
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
    void ratingAgainReplacesTheReviewersEarlierRating() throws Exception {
        String firstId = rate(rateRequest().setComment("Great talk"));
        String secondId = rate(rateRequest().setValue(RateValue.TERRIBLE.getNumericValue()).setComment("Changed"));

        assertThat(secondId).isEqualTo(firstId);
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings").with(SecurityHelper.admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratingCount").value(1))
                .andExpect(jsonPath("$.ratings[0].value").value("TERRIBLE"))
                .andExpect(jsonPath("$.ratings[0].comment").value("Changed"));
    }

    @Test
    void aStoredRatingCannotBeChangedByPuttingToIt() throws Exception {
        String ratingId = rate(rateRequest().setComment("Great talk"));

        // No handler maps this path for any method any more, so it is 404 rather than 405.
        mockMvc.perform(put("/presentations/" + presentation.getId() + "/ratings/" + ratingId)
                        .content(json(new Rate().setValue(RateValue.TERRIBLE).setComment("Changed")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings").with(SecurityHelper.admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratingCount").value(1))
                .andExpect(jsonPath("$.ratings[0].value").value("AWESOME"))
                .andExpect(jsonPath("$.ratings[0].comment").value("Great talk"));
    }

    @Test
    void disablingRatingRequiresAdmin() throws Exception {
        mockMvc.perform(post("/presentations/" + presentation.getId() + "/rating/disable"))
                .andExpect(status().is4xxClientError());
    }

    private String rate(RateRequest request) throws Exception {
        String body = mockMvc.perform(post("/presentations/" + presentation.getId() + "/ratings")
                        .content(json(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    private RateRequest rateRequest() {
        return new RateRequest()
                .setReviewerToken(reviewerToken)
                .setValue(RateValue.AWESOME.getNumericValue());
    }
}
