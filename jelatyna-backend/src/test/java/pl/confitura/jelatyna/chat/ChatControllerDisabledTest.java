package pl.confitura.jelatyna.chat;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.confitura.jelatyna.BaseIntegrationTest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** With chat.enabled unset (default false) the endpoint is off: the spend kill-switch. */
class ChatControllerDisabledTest extends BaseIntegrationTest {

    @MockitoBean
    private DatalinksClient datalinksClient;

    @Test
    void returnsServiceUnavailableWhenChatDisabled() throws Exception {
        mockMvc.perform(post("/chat/ask")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"hi\"}"))
                .andExpect(status().isServiceUnavailable());
    }
}
