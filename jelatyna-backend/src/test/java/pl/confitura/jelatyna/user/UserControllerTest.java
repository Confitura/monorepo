package pl.confitura.jelatyna.user;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.ConferenceConfigurationProperties;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.api.model.WorkshopRequest;

// 403, not 401, once the call for papers closes: the admin app treats a 401 as a rejected token
// and signs the user out.
class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConferenceConfigurationProperties conference;

    private ZonedDateTime c4pEnd;

    @BeforeEach
    void closeTheCallForPapers() {
        c4pEnd = conference.getC4p().getEnd();
        conference.getC4p().setEnd(ZonedDateTime.now().minusDays(1));
    }

    @AfterEach
    void reopenTheCallForPapers() {
        conference.getC4p().setEnd(c4pEnd);
    }

    @Test
    void addingATalkAfterTheCallForPapersClosedIsForbidden() throws Exception {
        User speaker = userRepository.save(new User().setName("Late Speaker"));

        mockMvc.perform(post("/users/" + speaker.getId() + "/presentations")
                        .with(user(speaker))
                        .contentType(APPLICATION_JSON)
                        .content(json(new PresentationRequest("Late talk", "short", "long", "beginner", "Polish", new String[]{}))))
                .andExpect(status().isForbidden());
    }

    @Test
    void addingAWorkshopAfterTheCallForPapersClosedIsForbidden() throws Exception {
        User speaker = userRepository.save(new User().setName("Late Speaker"));

        mockMvc.perform(post("/users/" + speaker.getId() + "/workshops")
                        .with(user(speaker))
                        .contentType(APPLICATION_JSON)
                        .content(json(new WorkshopRequest("Late workshop", "short", "long", "beginner", "Polish", new String[]{}, true, 0.0, 180, 20))))
                .andExpect(status().isForbidden());
    }
}
