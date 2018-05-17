package pl.confitura.jelatyna.presentation.rating;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.AgendaEntry;
import pl.confitura.jelatyna.agenda.AgendaUtils;
import pl.confitura.jelatyna.agenda.PresentationUtils;
import pl.confitura.jelatyna.agenda.UserUtils;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.user.User;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private User user;
    private List<AgendaEntry> agenda;
    private Presentation presentation;
    private Rate rate;

    @BeforeEach
    public void setUp() {
        user = userUtils.createUser("user");
        agenda = agendaUtils.createAgenda(
                new String[]{"", "room1", "room2"},
                new String[]{"09-12", "presentation1", "presentation2"},
                new String[]{"12-18", "presentation3", "presentation4"}
        );
        presentation = agenda.get(0).getPresentation();
        rate = new Rate().setValue(RateValue.AWESOME);
    }


    @Test
    @Transactional
    public void userShouldBeAbleToRate() throws Exception {

        // when user rates presentation
        rate(presentation, user, rate)

                // then rate is created
                .andExpect(jsonPath("$.value").value(rate.getValue().name()))
                .andExpect(status().isCreated());

        // and rate is added to presentation
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rates").value(hasSize(1)))
                .andExpect(jsonPath("$._embedded.rates[0].value").value(rate.getValue().name()));
    }

    @Test
    @Transactional
    public void userShouldBeAbleToAddComment() throws Exception {

        Rate rate = new Rate().setValue(RateValue.AWESOME).setComment("comment");
        // when user rates presentation with comment
        rate(presentation, user, rate)
                .andExpect(status().isCreated());

        // then comment is added to presentation
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rates").value(hasSize(1)))
                .andExpect(jsonPath("$._embedded.rates[0].comment").value(rate.getComment()));
    }

    @Test
    @Transactional
    public void userShouldNotBeAbleToRateSamePresentationTwice() throws Exception {

        rate(presentation, user, rate);

        //when same user tries to rate same presentation
        rate(presentation, user, rate)

                //then system denies it
                .andExpect(status().isConflict());


        //and new rate is not added
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$._embedded.rates").value(hasSize(1)));
    }

    @Test
    @Transactional
    public void userShouldBeAbleToUpdateRate() throws Exception {

        Rate newRate = new Rate().setValue(RateValue.GREAT);
        MvcResult mvcResult = rate(presentation, user, rate).andReturn();
        Rate createdRate = fromJson(mvcResult.getResponse().getContentAsString(), Rate.class);

        // when user updates rate
        mockMvc.perform(
                put("/presentations/" + presentation.getId() + "/ratings/" + createdRate.getId())
                        .with(user(user))
                        .content(json(newRate))
                        .contentType(HAL_JSON)
        )
                .andExpect(status().isOk());

        //then vote is updated
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rates").value(hasSize(1)))
                .andExpect(jsonPath("$._embedded.rates[0].value").value(newRate.getValue().name()));
    }


    @Test
    @Transactional
    public void userShouldBeAbleToRateSecondPresentation() throws Exception {
        rate(presentation, user, rate);

        Rate secondRate = new Rate().setValue(RateValue.GREAT);
        Presentation secondPresentation = agenda.get(1).getPresentation();

        //when second presentaion is rated
        rate(secondPresentation, user, secondRate);


        // then rate is added to presentation
        mockMvc.perform(get("/presentations/" + secondPresentation.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rates").value(hasSize(1)))
                .andExpect(jsonPath("$._embedded.rates[0].value").value(secondRate.getValue().name()));

    }

    @Test
    @Transactional
    public void otherUserShouldBeAbleToRateSamePresentation() throws Exception {
        rate(presentation, user, rate);

        User otherUser = userUtils.createUser("other user");
        Rate otherRate = new Rate().setValue(RateValue.GREAT);

        //when other user rates presentation
        rate(presentation, otherUser, otherRate);

        //then presantation has added rate;
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rates").value(hasSize(2)));


    }

    private ResultActions rate(Presentation presentation, User user, Rate rate) throws Exception {
        return mockMvc.perform(
                post("/presentations/" + presentation.getId() + "/ratings")
                        .with(user(user))
                        .content(json(rate))
                        .contentType(HAL_JSON)
        );
    }
}