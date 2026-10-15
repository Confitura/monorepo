package pl.confitura.jelatyna.chat;

/** Small shared types for the chat feature. */
public final class ChatTypes {

    private ChatTypes() {
    }

    /** Incoming request from the browser. */
    public record AskRequest(String question, String conversationId) {
    }

    /** A Server-Sent Event: an event name and its JSON data payload. */
    public record SseEvent(String event, String data) {
    }

    /** Where relayed events are written (the browser SSE stream, or a test capture). */
    @FunctionalInterface
    public interface EventSink {
        void send(SseEvent event) throws Exception;
    }
}
