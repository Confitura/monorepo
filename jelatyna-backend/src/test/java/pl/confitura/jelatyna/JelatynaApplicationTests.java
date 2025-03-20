package pl.confitura.jelatyna;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import pl.confitura.jelatyna.agenda.Room;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

class JelatynaApplicationTests extends BaseIntegrationTest {

    @Test
    void dataRestJpaWorks() throws Exception {

        mockMvc
                .perform(
                        get("/")
                                .accept(MediaTypes.HAL_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON));
    }


    @Test
    void securityWorks() throws Exception {
        Room room = new Room().setId("id").setLabel("room1").setDisplayOrder(1);
        mockMvc
                .perform(
                        post("/rooms/")
                                .with(user("user"))
                                .content(json(room))
                                .accept(MediaTypes.HAL_JSON)
                )
                .andExpect(status().isForbidden());

        mockMvc
                .perform(
                        post("/rooms/")
                                .content(json(room))
                                .accept(MediaTypes.HAL_JSON)
                                .with(admin())
                )
                .andExpect(status().isCreated());
    }

}
