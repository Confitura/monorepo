package pl.confitura.jelatyna.chat;

import pl.confitura.jelatyna.chat.ChatTypes.SseEvent;

import java.util.function.Consumer;

/** Calls the Datalinks AutoRAG streaming endpoint and relays its SSE events. */
public interface DatalinksClient {

    /**
     * Runs a query against Datalinks and invokes {@code onEvent} for every SSE
     * event as it arrives. The Datalinks token is held by the implementation and
     * is never passed to the caller.
     *
     * @param query          the (already name→id translated) question
     * @param conversationId prior conversation to continue, or {@code null} to start a new one
     */
    void ask(String query, String conversationId, Consumer<SseEvent> onEvent);
}
