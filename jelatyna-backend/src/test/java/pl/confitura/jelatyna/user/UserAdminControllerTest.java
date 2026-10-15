package pl.confitura.jelatyna.user;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAdminControllerTest extends BaseIntegrationTest {

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
