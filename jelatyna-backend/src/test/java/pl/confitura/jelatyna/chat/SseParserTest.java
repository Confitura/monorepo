package pl.confitura.jelatyna.chat;

import org.junit.jupiter.api.Test;
import pl.confitura.jelatyna.chat.ChatTypes.SseEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SseParserTest {

    private final SseParser parser = new SseParser();

    private List<SseEvent> parse(String raw) throws IOException {
        List<SseEvent> events = new ArrayList<>();
        parser.parse(new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8)), events::add);
        return events;
    }

    @Test
    void parsesEventNameAndData() throws IOException {
        List<SseEvent> events = parse("event: answer\ndata: {\"response\":\"hi\"}\n\n");
        assertThat(events).hasSize(1);
        assertThat(events.get(0).event()).isEqualTo("answer");
        assertThat(events.get(0).data()).isEqualTo("{\"response\":\"hi\"}");
    }

    @Test
    void framesEachEventFullyEvenWhenSpeakerIdWouldSpanReads() throws IOException {
        // The whole event (blank-line terminated) is assembled before delivery,
        // so an id like "spk-1" is always intact by the time it is processed.
        String raw = "event: plan\ndata: step one\n\nevent: answer\ndata: {\"response\":\"talk by spk-1\"}\n\n";
        List<SseEvent> events = parse(raw);
        assertThat(events).hasSize(2);
        assertThat(events.get(1).data()).contains("spk-1");
    }

    @Test
    void reassemblesEventWhenBytesArriveOneAtATimeSplittingAnId() throws IOException {
        // A drip stream returns a single byte per read, so a speaker id like
        // "spk-1" is split mid-token at the transport level. The parser must
        // still deliver one intact event.
        String raw = "event: answer\ndata: {\"response\":\"talk by spk-1\"}\n\n";
        InputStream drip = new java.io.InputStream() {
            private final byte[] bytes = raw.getBytes(StandardCharsets.UTF_8);
            private int i = 0;

            @Override
            public int read() {
                return i < bytes.length ? (bytes[i++] & 0xff) : -1;
            }

            @Override
            public int read(byte[] b, int off, int len) {
                int c = read();
                if (c == -1) return -1;
                b[off] = (byte) c;
                return 1; // force one byte per read
            }
        };
        List<SseEvent> events = new ArrayList<>();
        parser.parse(drip, events::add);
        assertThat(events).hasSize(1);
        assertThat(events.get(0).data()).contains("spk-1");
    }

    @Test
    void flushesTrailingEventWithoutBlankLine() throws IOException {
        List<SseEvent> events = parse("event: answer\ndata: {\"response\":\"bye\"}");
        assertThat(events).hasSize(1);
        assertThat(events.get(0).data()).contains("bye");
    }
}
