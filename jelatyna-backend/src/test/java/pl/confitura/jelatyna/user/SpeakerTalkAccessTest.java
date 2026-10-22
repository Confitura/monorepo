package pl.confitura.jelatyna.user;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

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
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.api.model.WorkshopRequest;
import pl.confitura.jelatyna.edition.EditionRepository;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

// The /users/{userId}/presentations/{id} and /users/{userId}/workshops/{id} endpoints serve only
// a talk or workshop that user speaks at (as speaker or co-speaker), and only through the
// endpoint of its kind; admins reach every one. Every such endpoint is listed in Endpoint.
@Transactional
class SpeakerTalkAccessTest extends BaseIntegrationTest {

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private UserRepository userRepository;

    private User speaker;
    private User coSpeaker;
    private User otherSpeaker;

    enum Endpoint {
        READ_TALK(false), EDIT_TALK(false), DELETE_TALK(false),
        READ_WORKSHOP(true), EDIT_WORKSHOP(true), DELETE_WORKSHOP(true);

        final boolean workshop;

        Endpoint(boolean workshop) {
            this.workshop = workshop;
        }
    }

    @BeforeEach
    void speakers() {
        speaker = userRepository.save(new User().setName("Speaker"));
        coSpeaker = userRepository.save(new User().setName("Co-speaker"));
        otherSpeaker = userRepository.save(new User().setName("Other Speaker"));
    }

    @ParameterizedTest
    @EnumSource
    void aSpeakerCanReachTheirOwnTalk(Endpoint endpoint) throws Exception {
        Presentation talk = talk(endpoint.workshop);

        call(endpoint, speaker, talk, user(speaker)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @EnumSource
    void aCoSpeakerCanReachTheirTalk(Endpoint endpoint) throws Exception {
        Presentation talk = talk(endpoint.workshop);

        call(endpoint, coSpeaker, talk, user(coSpeaker)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @EnumSource
    void aSpeakerCannotReachAnotherSpeakersTalk(Endpoint endpoint) throws Exception {
        Presentation talk = talk(endpoint.workshop);

        call(endpoint, otherSpeaker, talk, user(otherSpeaker)).andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @EnumSource
    void anAdminCanReachAnySpeakersTalk(Endpoint endpoint) throws Exception {
        Presentation talk = talk(endpoint.workshop);

        call(endpoint, speaker, talk, admin()).andExpect(status().isOk());
    }

    @ParameterizedTest
    @EnumSource
    void aTalkOfTheOtherKindIsRefused(Endpoint endpoint) throws Exception {
        Presentation talk = talk(!endpoint.workshop);

        call(endpoint, speaker, talk, user(speaker)).andExpect(status().isForbidden());
        call(endpoint, speaker, talk, admin()).andExpect(status().isForbidden());
    }

    private ResultActions call(Endpoint endpoint, User pathUser, Presentation talk, RequestPostProcessor signedIn)
            throws Exception {
        String talks = "/users/" + pathUser.getId() + "/presentations/" + talk.getId();
        String workshops = "/users/" + pathUser.getId() + "/workshops/" + talk.getId();
        MockHttpServletRequestBuilder request = switch (endpoint) {
            case READ_TALK -> get(talks);
            case EDIT_TALK -> post(talks).contentType(APPLICATION_JSON)
                    .content(json(new PresentationRequest("Edited", "short", "long", "beginner", "Polish", new String[]{})));
            case DELETE_TALK -> delete(talks);
            case READ_WORKSHOP -> get(workshops);
            case EDIT_WORKSHOP -> post(workshops).contentType(APPLICATION_JSON)
                    .content(json(new WorkshopRequest("Edited", "short", "long", "beginner", "Polish", new String[]{}, true, 0.0, 180, 20)));
            case DELETE_WORKSHOP -> delete(workshops);
        };
        return mockMvc.perform(request.with(signedIn));
    }

    private Presentation talk(boolean workshop) {
        return presentationRepository.save(new Presentation()
                .setTitle("Talk")
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("beginner")
                .setLanguage("Polish")
                .setWorkshop(workshop)
                .setSpeakers(new HashSet<>(Set.of(speaker, coSpeaker)))
                .setEdition(editionRepository.findCurrent()));
    }
}
