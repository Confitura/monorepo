package pl.confitura.jelatyna.user;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

class UserControllerTest extends BaseIntegrationTest {

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
    void aUserUploadsTheirProfilePicture() throws Exception {
        User speaker = userRepository.save(new User().setName("Speaker"));

        uploadProfilePicture(speaker, user(speaker))
                .andExpect(status().isOk())
                .andExpect(content().string(endsWith("/photos/" + speaker.getId() + ".png")));
    }

    @Test
    void refusesAProfilePictureForSomeoneElse() throws Exception {
        User speaker = userRepository.save(new User().setName("Speaker"));

        uploadProfilePicture(speaker, user("someone-else")).andExpect(status().isForbidden());
    }

    private ResultActions uploadProfilePicture(User owner, RequestPostProcessor signedIn) throws Exception {
        MockMultipartFile photo = new MockMultipartFile("file", "me.png", "image/png", new byte[]{1, 2, 3});
        return mockMvc.perform(multipart("/resources/" + owner.getId()).file(photo).with(signedIn));
    }
}
