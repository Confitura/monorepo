package pl.confitura.jelatyna.conference.registration.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.identity.user.web.SecurityHelper.user;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRequest;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

class UserWithTicketControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private EditionRepository editionRepository;

    @Test
    void aUsersProfileSaysWhetherTheySpeak() throws Exception {
        User speaker = userRepository.save(new User().setName("Speaker"));
        presentationRepository.save(Presentation.from(new PresentationRequest("Talk", "short", "long", "beginner", "Polish",
                new String[]{}), speaker, Set.of()).setEdition(editionRepository.findCurrent()));
        User listener = userRepository.save(new User().setName("Listener"));

        mockMvc.perform(get("/users/" + speaker.getId()).with(user(speaker)))
                .andExpect(jsonPath("$.speaker").value(true));
        mockMvc.perform(get("/current-user").with(user(listener)))
                .andExpect(jsonPath("$.speaker").value(false));
    }

    @Test
    void aUserSavesTheirProfile() throws Exception {
        User owner = userRepository.save(new User().setName("Owner"));

        saveProfile(owner, owner)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(owner.getId()))
                .andExpect(jsonPath("$.name").value("Renamed"))
                .andExpect(jsonPath("$.participant").value(false))
                .andExpect(jsonPath("$.speaker").value(false));
    }

    @Test
    void nobodyElseSavesAUsersProfile() throws Exception {
        User owner = userRepository.save(new User().setName("Owner"));
        User other = userRepository.save(new User().setName("Other"));

        saveProfile(owner, other).andExpect(status().isForbidden());
    }

    private ResultActions saveProfile(User owner, User actor) throws Exception {
        return mockMvc.perform(post("/users").with(user(actor)).contentType(APPLICATION_JSON)
                .content("{\"id\":\"" + owner.getId() + "\",\"name\":\"Renamed\",\"email\":\"owner@example.com\"}"));
    }
}
