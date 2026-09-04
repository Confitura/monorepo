package pl.confitura.jelatyna.chat;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.confitura.jelatyna.chat.ChatTypes.SseEvent;

import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class HttpDatalinksClientTest {

    private HttpServer server;
    private final AtomicReference<String> capturedBody = new AtomicReference<>();
    private final AtomicReference<String> capturedAuth = new AtomicReference<>();
    private final AtomicReference<String> capturedPath = new AtomicReference<>();

    @BeforeEach
    void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/query/ask", exchange -> {
            capturedPath.set(exchange.getRequestURI().getPath());
            capturedAuth.set(exchange.getRequestHeaders().getFirst("Authorization"));
            try (InputStream in = exchange.getRequestBody()) {
                capturedBody.set(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            }
            byte[] sse = ("event: answer\ndata: {\"response\":\"hi from spk-1\"}\n\n")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
            exchange.sendResponseHeaders(200, sse.length);
            exchange.getResponseBody().write(sse);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    private HttpDatalinksClient client() {
        ChatConfigurationProperties props = new ChatConfigurationProperties();
        ChatConfigurationProperties.Datalinks dl = props.getDatalinks();
        dl.setBaseUrl("http://localhost:" + server.getAddress().getPort());
        dl.setToken("secret-token");
        dl.setUsername("confitura");
        dl.setNamespace("confitura-2026");
        return new HttpDatalinksClient(props, JsonMapper.builder().build());
    }

    @Test
    void sendsBearerTokenAndDisablesWebSearch() {
        client().ask("who speaks about kafka?", null, e -> { });

        assertThat(capturedPath.get()).isEqualTo("/query/ask");
        assertThat(capturedAuth.get()).isEqualTo("Bearer secret-token");
        assertThat(capturedBody.get())
                .contains("\"webSearch\":false")
                .contains("\"query\":\"who speaks about kafka?\"")
                .contains("\"namespace\":\"confitura-2026\"");
    }

    @Test
    void streamsParsedEventsWithoutLeakingTheToken() {
        List<SseEvent> events = new ArrayList<>();
        client().ask("q", null, events::add);

        assertThat(events).hasSize(1);
        assertThat(events.get(0).event()).isEqualTo("answer");
        // The token is sent upstream but never surfaces in the relayed events.
        assertThat(events.get(0).data()).doesNotContain("secret-token");
    }

    @Test
    void omitsConversationIdWhenNotProvided() {
        client().ask("q", null, e -> { });
        assertThat(capturedBody.get()).doesNotContain("conversationId");
    }
}
