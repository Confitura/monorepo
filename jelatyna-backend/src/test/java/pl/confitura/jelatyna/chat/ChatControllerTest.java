package pl.confitura.jelatyna.chat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.chat.ChatTypes.SseEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

@TestPropertySource(properties = {
        "chat.enabled=true",
        "chat.rate-limit-per-minute=1",
        "chat.datalinks.token=secret-token",
        "chat.datalinks.base-url=http://localhost:1/api/v1",
        "chat.datalinks.username=confitura",
        "chat.datalinks.namespace=confitura-2026"
})
@Import(ChatControllerTest.SameThreadExecutorConfig.class)
class ChatControllerTest extends BaseIntegrationTest {

    @MockitoBean
    private DatalinksClient datalinksClient;

    @MockitoBean
    private SpeakerDirectory speakerDirectory;

    @Test
    void relaysAnswerWithSpeakerNamesAndWithoutLeakingTheToken() throws Exception {
        when(speakerDirectory.idToName()).thenReturn(Map.of("spk-1", "Artur Laskowski"));
        stubDatalinksAnswer("{\"response\":\"The talk is by spk-1\"}");

        var mvcResult = mockMvc.perform(post("/chat/ask")
                        .header("X-Forwarded-For", "10.0.0.1")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"who speaks about kafka?\"}"))
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Artur Laskowski")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("spk-1"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("secret-token"))));
    }

    @Test
    void translatesSpeakerNameInQuestionToIdBeforeCallingDatalinks() throws Exception {
        when(speakerDirectory.idToName()).thenReturn(Map.of("spk-1", "Artur Laskowski"));
        stubDatalinksAnswer("{\"response\":\"ok\"}");

        var mvcResult = mockMvc.perform(post("/chat/ask")
                        .header("X-Forwarded-For", "10.0.0.2")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"What is Artur Laskowski presenting?\"}"))
                .andReturn();
        mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk());

        org.mockito.Mockito.verify(datalinksClient)
                .ask(org.mockito.ArgumentMatchers.contains("spk-1"), any(), any());
    }

    @Test
    void rejectsWithTooManyRequestsWhenRateLimitExceeded() throws Exception {
        when(speakerDirectory.idToName()).thenReturn(Map.of());
        stubDatalinksAnswer("{\"response\":\"ok\"}");

        // rate-limit-per-minute=1: the second request from the same client is throttled.
        var first = mockMvc.perform(post("/chat/ask")
                        .header("X-Forwarded-For", "10.0.0.3")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"first\"}"))
                .andReturn();
        mockMvc.perform(asyncDispatch(first)).andExpect(status().isOk());

        mockMvc.perform(post("/chat/ask")
                        .header("X-Forwarded-For", "10.0.0.3")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"second\"}"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void rejectsOverlongQuestionWithPayloadTooLarge() throws Exception {
        String longQuestion = "q".repeat(600); // default max is 500
        mockMvc.perform(post("/chat/ask")
                        .header("X-Forwarded-For", "10.0.0.4")
                        .contentType(APPLICATION_JSON)
                        .content("{\"question\":\"" + longQuestion + "\"}"))
                .andExpect(status().isPayloadTooLarge());
    }

    private void stubDatalinksAnswer(String answerJson) {
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Consumer<SseEvent> sink = invocation.getArgument(2);
            for (SseEvent e : List.of(new SseEvent("answer", answerJson))) {
                sink.accept(e);
            }
            return null;
        }).when(datalinksClient).ask(any(), any(), any());
    }

    @TestConfiguration
    static class SameThreadExecutorConfig {
        @Bean
        @Primary
        ExecutorService sameThreadChatExecutor() {
            return new AbstractExecutorService() {
                @Override public void execute(Runnable command) { command.run(); }
                @Override public void shutdown() { }
                @Override public List<Runnable> shutdownNow() { return List.of(); }
                @Override public boolean isShutdown() { return false; }
                @Override public boolean isTerminated() { return false; }
                @Override public boolean awaitTermination(long timeout, TimeUnit unit) { return true; }
            };
        }
    }
}
