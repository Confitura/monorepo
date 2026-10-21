package pl.confitura.jelatyna.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAdminControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Test
    void listsEachUsersLoginsAndNoneForAManualUser() throws Exception {
        User twoLogins = userRepository.save(new User().setName("Signed in twice").setOrigin("google"));
        Login google = loginRepository.save(new Login(twoLogins, "google", UUID.randomUUID().toString()));
        Login github = loginRepository.save(new Login(twoLogins, "github", UUID.randomUUID().toString()));
        User manual = userRepository.save(new User().setName("Added by an admin").setOrigin("manual"));

        SecurityHelper.asAdmin();
        String body = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        SecurityHelper.cleanSecurity();

        UserAdminController.FullUser[] users = fromJson(body, UserAdminController.FullUser[].class);
        assertThat(Arrays.stream(users).filter(user -> user.id().equals(twoLogins.getId())))
                .singleElement()
                // Ids too: an admin unlinks a Login from this list.
                .satisfies(user -> assertThat(user.logins()).extracting(LoginDto::id, LoginDto::provider)
                        .containsExactly(tuple(github.getId(), "github"), tuple(google.getId(), "google")));
        assertThat(Arrays.stream(users).filter(user -> user.id().equals(manual.getId())))
                .singleElement()
                .satisfies(user -> assertThat(user.logins()).isEmpty());
    }

    @Test
    void createManualPersistsAdminAndVolunteerFlags() throws Exception {
        SecurityHelper.asAdmin();
        // The User JSON keys are `admin`/`volunteer` (Lombok's isAdmin() getter drops the `is`).
        mockMvc.perform(post("/users/manual")
                        .content("{\"name\":\"Jane\",\"email\":\"jane@example.com\",\"admin\":true,\"volunteer\":true}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAdmin").value(true))
                .andExpect(jsonPath("$.isVolunteer").value(true));
        SecurityHelper.cleanSecurity();
    }

    @Test
    void createManualDefaultsFlagsToFalse() throws Exception {
        SecurityHelper.asAdmin();
        mockMvc.perform(post("/users/manual")
                        .content("{\"name\":\"Bob\",\"email\":\"bob@example.com\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAdmin").value(false))
                .andExpect(jsonPath("$.isVolunteer").value(false));
        SecurityHelper.cleanSecurity();
    }
}
