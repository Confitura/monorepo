package pl.confitura.jelatyna.user;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import pl.confitura.jelatyna.BaseIntegrationTest;

class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

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
