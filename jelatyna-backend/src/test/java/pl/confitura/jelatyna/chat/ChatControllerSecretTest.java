package pl.confitura.jelatyna.chat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.chat.ChatTypes.SseEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "chat.enabled=true",
        "chat.secret=topsecret",
        "chat.datalinks.token=secret-token",
        "chat.datalinks.base-url=http://localhost:1/api/v1",
        "chat.datalinks.username=confitura",
        "chat.datalinks.namespace=confitura-2026"
})
@Import(ChatControllerTest.SameThreadExecutorConfig.class)
class ChatControllerSecretTest extends BaseIntegrationTest {

    @MockitoBean
    private DatalinksClient datalinksClient;

    @MockitoBean
    private SpeakerDirectory speakerDirectory;

    @Test
    void rejectsRequestWithoutTheSecretHeader() throws Exception {
        mockMvc.perform(post("/chat/ask")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"hi\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsRequestWithWrongSecret() throws Exception {
        mockMvc.perform(post("/chat/ask")
                        .header("X-Chat-Secret", "nope")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"hi\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void allowsRequestWithCorrectSecret() throws Exception {
        when(speakerDirectory.idToName()).thenReturn(Map.of());
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Consumer<SseEvent> sink = invocation.getArgument(2);
            for (SseEvent e : List.of(new SseEvent("answer", "{\"response\":\"ok\"}"))) {
                sink.accept(e);
            }
            return null;
        }).when(datalinksClient).ask(any(), any(), any());

        var result = mockMvc.perform(post("/chat/ask")
                        .header("X-Chat-Secret", "topsecret")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"hi\"}"))
                .andReturn();
        mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk());
    }
}
