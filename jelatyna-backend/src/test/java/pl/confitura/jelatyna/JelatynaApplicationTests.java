package pl.confitura.jelatyna;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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


        mockMvc
                .perform(
                        delete("/rooms/1")
                                .accept(MediaTypes.HAL_JSON)
                )
                .andExpect(status().isForbidden());
    }

}
