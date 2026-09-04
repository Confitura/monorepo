package pl.confitura.jelatyna.chat;

import org.junit.jupiter.api.Test;
import pl.confitura.jelatyna.chat.ChatTypes.AskRequest;
import pl.confitura.jelatyna.chat.ChatTypes.SseEvent;

import tools.jackson.databind.json.JsonMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChatServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-04T10:00:00Z"), ZoneOffset.UTC);
    private static final Map<String, String> SPEAKERS = Map.of("spk-1", "Artur Laskowski");

    /** Records the query it received and replays canned events. */
    static class FakeDatalinks implements DatalinksClient {
        int calls = 0;
        String lastQuery;
        String lastConversationId;
        List<SseEvent> events = List.of(new SseEvent("answer", "{\"response\":\"The talk is by spk-1\"}"));

        @Override
        public void ask(String query, String conversationId, Consumer<SseEvent> onEvent) {
            calls++;
            lastQuery = query;
            lastConversationId = conversationId;
            events.forEach(onEvent);
        }
    }

    private ChatService service(ChatConfigurationProperties props, DatalinksClient client) {
        return new ChatService(
                props,
                client,
                () -> SPEAKERS,
                new RateLimiter(props.getRateLimitPerMinute(), CLOCK),
                new MonthlyGate(props.getMonthlyCallCap(), CLOCK),
                new AnswerCache(50),
                JsonMapper.builder().build());
    }

    private ChatConfigurationProperties enabledProps() {
        ChatConfigurationProperties p = new ChatConfigurationProperties();
        p.setEnabled(true);
        p.setMaxQuestionLength(500);
        p.setRateLimitPerMinute(10);
        p.setMonthlyCallCap(1000);
        return p;
    }

    private List<SseEvent> collect() {
        return new ArrayList<>();
    }

    @Test
    void rewritesSpeakerNamesToIdsBeforeCallingDatalinks() throws Exception {
        FakeDatalinks client = new FakeDatalinks();
        ChatService service = service(enabledProps(), client);
        AskRequest req = new AskRequest("What is Artur Laskowski presenting?", null);

        service.preflight(req, "ip1");
        service.stream(req, collect()::add);

        assertThat(client.lastQuery).contains("spk-1").doesNotContain("Artur Laskowski");
    }

    @Test
    void rewritesSpeakerIdsToNamesInTheAnswer() throws Exception {
        FakeDatalinks client = new FakeDatalinks();
        ChatService service = service(enabledProps(), client);
        List<SseEvent> out = collect();
        AskRequest req = new AskRequest("who speaks about kafka?", null);

        service.preflight(req, "ip1");
        service.stream(req, out::add);

        SseEvent answer = out.stream().filter(e -> e.event().equals("answer")).findFirst().orElseThrow();
        assertThat(answer.data()).contains("Artur Laskowski").doesNotContain("spk-1");
    }

    @Test
    void servesRepeatedQuestionFromCacheWithoutCallingDatalinksAgain() throws Exception {
        FakeDatalinks client = new FakeDatalinks();
        ChatService service = service(enabledProps(), client);
        AskRequest req = new AskRequest("what talks are about kafka?", null);

        service.preflight(req, "ip1");
        service.stream(req, collect()::add);
        service.preflight(req, "ip1");
        service.stream(req, collect()::add);

        assertThat(client.calls).isEqualTo(1);
    }

    @Test
    void rejectsWhenDisabled() {
        ChatConfigurationProperties props = enabledProps();
        props.setEnabled(false);
        ChatService service = service(props, new FakeDatalinks());
        assertThatThrownBy(() -> service.preflight(new AskRequest("hi", null), "ip1"))
                .isInstanceOf(ChatException.class)
                .extracting(e -> ((ChatException) e).reason())
                .isEqualTo(ChatException.Reason.DISABLED);
    }

    @Test
    void rejectsOverlongQuestionWithoutCallingDatalinks() {
        ChatConfigurationProperties props = enabledProps();
        props.setMaxQuestionLength(5);
        FakeDatalinks client = new FakeDatalinks();
        ChatService service = service(props, client);
        assertThatThrownBy(() -> service.preflight(new AskRequest("way too long question", null), "ip1"))
                .isInstanceOf(ChatException.class)
                .extracting(e -> ((ChatException) e).reason())
                .isEqualTo(ChatException.Reason.TOO_LONG);
        assertThat(client.calls).isZero();
    }

    @Test
    void rejectsWhenRateLimitExceeded() {
        ChatConfigurationProperties props = enabledProps();
        props.setRateLimitPerMinute(1);
        ChatService service = service(props, new FakeDatalinks());
        service.preflight(new AskRequest("first", null), "ip1");
        assertThatThrownBy(() -> service.preflight(new AskRequest("second", null), "ip1"))
                .isInstanceOf(ChatException.class)
                .extracting(e -> ((ChatException) e).reason())
                .isEqualTo(ChatException.Reason.RATE_LIMITED);
    }

    @Test
    void rejectsWhenMonthlyCapReached() {
        ChatConfigurationProperties props = enabledProps();
        props.setMonthlyCallCap(0);
        ChatService service = service(props, new FakeDatalinks());
        assertThatThrownBy(() -> service.preflight(new AskRequest("hi", null), "ip1"))
                .isInstanceOf(ChatException.class)
                .extracting(e -> ((ChatException) e).reason())
                .isEqualTo(ChatException.Reason.CAP_REACHED);
    }

    @Test
    void passesConversationIdThroughForFollowUps() throws Exception {
        FakeDatalinks client = new FakeDatalinks();
        ChatService service = service(enabledProps(), client);
        AskRequest req = new AskRequest("and where is it?", "conv-42");

        service.preflight(req, "ip1");
        service.stream(req, collect()::add);

        assertThat(client.lastConversationId).isEqualTo("conv-42");
    }
}
