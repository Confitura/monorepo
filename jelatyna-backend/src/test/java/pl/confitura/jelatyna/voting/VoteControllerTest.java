package pl.confitura.jelatyna.voting;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import pl.confitura.jelatyna.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VoteControllerTest extends BaseIntegrationTest {

    @Test
    public void shouldStartVoting() throws Exception {

        //when
        ResultActions $ = mockMvc.perform(post("/votes/start/token" ));

        //then
        $.andExpect(status().isOk());

    }

}