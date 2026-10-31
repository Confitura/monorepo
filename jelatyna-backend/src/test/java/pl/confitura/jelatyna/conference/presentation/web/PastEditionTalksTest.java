package pl.confitura.jelatyna.conference.presentation.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRequest;
import pl.confitura.jelatyna.conference.presentation.domain.WorkshopRequest;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

// A past Edition's talks and workshops are read-only for their speakers; admins can still change
// them (ADR-0004). Every speaker endpoint that changes an existing talk is listed in Change.
@Transactional
class PastEditionTalksTest extends BaseIntegrationTest {

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private UserRepository userRepository;

    private User speaker;
    private User coSpeaker;

    enum Change { EDIT_TALK, DELETE_TALK, EDIT_WORKSHOP, DELETE_WORKSHOP, ADD_CO_SPEAKER, REMOVE_CO_SPEAKER }

    @BeforeEach
    void speakers() {
        speaker = userRepository.save(new User().setName("Speaker").setEmail("speaker@past-edition.test"));
        coSpeaker = userRepository.save(new User().setName("Co-speaker").setEmail("co-speaker@past-edition.test"));
        userRepository.save(new User().setName("Newcomer").setEmail("newcomer@past-edition.test"));
    }

    @ParameterizedTest
    @EnumSource
    void aSpeakerCannotChangeAPastEditionsTalk(Change change) throws Exception {
        Presentation talk = talk(pastEdition(), change);

        make(change, talk, user(speaker)).andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @EnumSource
    void anAdminCanChangeAPastEditionsTalk(Change change) throws Exception {
        Presentation talk = talk(pastEdition(), change);

        make(change, talk, admin()).andExpect(status().isOk());
    }

    @ParameterizedTest
    @EnumSource
    void aSpeakerCanChangeACurrentEditionsTalk(Change change) throws Exception {
        Presentation talk = talk(editionRepository.findCurrent(), change);

        make(change, talk, user(speaker)).andExpect(status().isOk());
    }

    private ResultActions make(Change change, Presentation talk, RequestPostProcessor signedIn) throws Exception {
        String talks = "/users/" + speaker.getId() + "/presentations/" + talk.getId();
        String workshops = "/users/" + speaker.getId() + "/workshops/" + talk.getId();
        String coSpeakers = "/presentations/" + talk.getId() + "/cospeakers/";
        MockHttpServletRequestBuilder request = switch (change) {
            case EDIT_TALK -> post(talks).contentType(APPLICATION_JSON)
                    .content(json(new PresentationRequest("Edited", "short", "long", "beginner", "Polish", new String[]{})));
            case DELETE_TALK -> delete(talks);
            case EDIT_WORKSHOP -> post(workshops).contentType(APPLICATION_JSON)
                    .content(json(new WorkshopRequest("Edited", "short", "long", "beginner", "Polish", new String[]{}, true, 0.0, 180, 20)));
            case DELETE_WORKSHOP -> delete(workshops);
            case ADD_CO_SPEAKER -> post(coSpeakers + "newcomer@past-edition.test");
            case REMOVE_CO_SPEAKER -> delete(coSpeakers + coSpeaker.getId());
        };
        return mockMvc.perform(request.with(signedIn));
    }

    private Presentation talk(Edition edition, Change change) {
        boolean workshop = change == Change.EDIT_WORKSHOP || change == Change.DELETE_WORKSHOP;
        return presentationRepository.save(new Presentation()
                .setTitle("Talk")
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("beginner")
                .setLanguage("Polish")
                .setWorkshop(workshop)
                .setSpeakers(new HashSet<>(Set.of(speaker, coSpeaker)))
                .setEdition(edition));
    }

    private Edition pastEdition() {
        SecurityHelper.asAdmin();
        Edition edition = editionRepository.save(new Edition()
                .setYear(2025)
                .setName("Confitura 2025")
                .setConferenceStart(Instant.parse("2025-06-28T07:00:00Z"))
                .setLastDay(LocalDate.of(2025, 6, 28)));
        SecurityHelper.cleanSecurity();
        return edition;
    }
}
