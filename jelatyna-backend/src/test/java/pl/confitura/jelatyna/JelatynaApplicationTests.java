package pl.confitura.jelatyna;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import pl.confitura.jelatyna.agenda.Day;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

class JelatynaApplicationTests extends BaseIntegrationTest {


    @Test
    void securityWorks() throws Exception {
        Day day = new Day().setId("security-day").setLabel("day1").setDate(LocalDate.now()).setDisplayOrder(1);
        mockMvc
                .perform(
                        post("/days")
                                .with(user("user"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(day))
                )
                .andExpect(status().isForbidden());

        mockMvc
                .perform(
                        post("/days")
                                .content(json(day))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(admin())
                )
                .andExpect(status().isCreated());
    }

}
