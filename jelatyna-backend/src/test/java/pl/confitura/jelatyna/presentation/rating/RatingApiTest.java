package pl.confitura.jelatyna.presentation.rating;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.AgendaEntry;
import pl.confitura.jelatyna.agenda.AgendaUtils;
import pl.confitura.jelatyna.agenda.PresentationUtils;
import pl.confitura.jelatyna.agenda.UserUtils;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.RateRequest;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

class RatingApiTest extends BaseIntegrationTest {

    @Autowired
    AgendaUtils agendaUtils;
    @Autowired
    UserUtils userUtils;
    @Autowired
    PresentationUtils presentationUtils;

    private List<AgendaEntry> agenda;
    private Presentation presentation;
    private Rate rate = new Rate().setRate(RateValue.AWESOME);
    private String reviewerToken = UUID.randomUUID().toString();

    @BeforeEach
    public void setUp() {
        SecurityHelper.asAdmin();
        agenda = agendaUtils.createAgenda(
                new String[]{"", "room1", "room2"},
                new String[]{"09-12", "presentation1", "presentation2"},
                new String[]{"12-18", "presentation3", "presentation4"}
        );
        presentation = agenda.get(0).getPresentation();
        SecurityHelper.cleanSecurity();
    }

    @Test
    @Transactional
    public void userShouldBeAbleToRate() throws Exception {

        // when user rates presentation
        rate(presentation, reviewerToken, rate)

                // then rate is created
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rate").value(rate.getRate().name()));

        // and rate is added to presentation
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].rate").value(rate.getRate().name()));
    }

    @Test
    @Transactional
    public void userShouldBeAbleToAddComment() throws Exception {

        Rate rate = new Rate().setRate(RateValue.AWESOME).setComment("comment");
        // when user rates presentation with comment
        rate(presentation, reviewerToken, rate)
                .andExpect(status().isCreated());

        // then comment is added to presentation
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].comment").value(rate.getComment()));
    }

    @Test
    @Transactional
    public void userShouldNotBeAbleToRateSamePresentationTwice() throws Exception {

        rate(presentation, reviewerToken, rate);

        //when same user tries to rate same presentation
        rate(presentation, reviewerToken, rate)

                //then system denies it
                .andExpect(status().isConflict());


        //and new rate is not added
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").value(hasSize(1)));
    }

    @Test
    @Transactional
    public void userShouldBeAbleToUpdateRate() throws Exception {

        Rate newRate = new Rate().setRate(RateValue.GREAT);
        MvcResult mvcResult = rate(presentation, reviewerToken, rate).andReturn();
        Rate createdRate = fromJson(mvcResult.getResponse().getContentAsString(), Rate.class);

        // when user updates rate
        mockMvc.perform(
                        put("/presentations/" + presentation.getId() + "/ratings/" + createdRate.getId())
                                .with(user(reviewerToken))
                                .content(json(newRate))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        //then vote is updated
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].rate").value(newRate.getRate().name()));
    }


    @Test
    @Transactional
    public void userShouldBeAbleToRateSecondPresentation() throws Exception {
        rate(presentation, reviewerToken, rate);

        Rate secondRate = new Rate().setRate(RateValue.GREAT);
        Presentation secondPresentation = agenda.get(1).getPresentation();

        //when second presentaion is rated
        rate(secondPresentation, reviewerToken, secondRate);


        // then rate is added to presentation
        mockMvc.perform(get("/presentations/" + secondPresentation.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].rate").value(secondRate.getRate().name()));

    }

    @Test
    @Transactional
    public void otherUserShouldBeAbleToRateSamePresentation() throws Exception {
        rate(presentation, reviewerToken, rate);
        Rate otherRate = new Rate().setRate(RateValue.GREAT);

        //when other user rates presentation
        rate(presentation, "other token", otherRate);

        //then presantation has added rate;
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").value(hasSize(2)));


    }

    private ResultActions rate(Presentation presentation, String token, Rate rate) throws Exception {
        RateRequest rateRequest = new RateRequest()
                .setId(rate.getId())
                .setReviewerToken(token)
                .setValue(rate.getRate().getNumericValue())
                .setComment(rate.getComment());
        return mockMvc.perform(
                post("/presentations/" + presentation.getId() + "/ratings")
                        .content(json(rateRequest))
                        .contentType(MediaType.APPLICATION_JSON)

        );
    }
}
