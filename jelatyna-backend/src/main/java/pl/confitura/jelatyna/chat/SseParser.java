package pl.confitura.jelatyna.chat;

import pl.confitura.jelatyna.chat.ChatTypes.SseEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Parses a Server-Sent Events byte stream into complete {@link SseEvent}s.
 * <p>
 * Events are framed by a blank line, so an event (and any speaker id inside its
 * data) is always fully assembled before it is handed to the consumer — even if
 * the underlying transport splits bytes in the middle of a field.
 */
public class SseParser {

    /** Reads {@code in} to completion, invoking {@code onEvent} for each complete event. */
    public void parse(InputStream in, Consumer<SseEvent> onEvent) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String eventName = null;
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    if (eventName != null || data.length() > 0) {
                        onEvent.accept(new SseEvent(eventName == null ? "message" : eventName, data.toString()));
                    }
                    eventName = null;
                    data.setLength(0);
                    continue;
                }
                if (line.startsWith("event:")) {
                    eventName = line.substring("event:".length()).trim();
                } else if (line.startsWith("data:")) {
                    if (data.length() > 0) {
                        data.append('\n');
                    }
                    data.append(line.substring("data:".length()).trim());
                }
                // other fields (id:, retry:, comments) are ignored
            }
            // flush a trailing event with no terminating blank line
            if (eventName != null || data.length() > 0) {
                onEvent.accept(new SseEvent(eventName == null ? "message" : eventName, data.toString()));
            }
        }
    }
}
