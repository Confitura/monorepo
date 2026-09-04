package pl.confitura.jelatyna.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.chat.ChatTypes.SseEvent;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

/** Datalinks client backed by the JDK {@link HttpClient} streaming the SSE body. */
@Slf4j
@Component
public class HttpDatalinksClient implements DatalinksClient {

    private final ChatConfigurationProperties.Datalinks config;
    private final ObjectMapper objectMapper;
    private final SseParser sseParser = new SseParser();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public HttpDatalinksClient(ChatConfigurationProperties properties, ObjectMapper objectMapper) {
        this.config = properties.getDatalinks();
        this.objectMapper = objectMapper;
    }

    @Override
    public void ask(String query, String conversationId, Consumer<SseEvent> onEvent) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("username", config.getUsername());
        body.put("namespace", config.getNamespace());
        body.put("query", query);
        body.put("webSearch", false);
        if (conversationId != null && !conversationId.isBlank()) {
            body.put("conversationId", conversationId);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getBaseUrl().replaceAll("/+$", "") + "/query/ask"))
                .header("Authorization", "Bearer " + config.getToken())
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .timeout(Duration.ofMinutes(2))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        try {
            HttpResponse<java.io.InputStream> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Datalinks /query/ask returned HTTP " + response.statusCode());
            }
            sseParser.parse(response.body(), onEvent);
        } catch (java.io.IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.warn("Datalinks /query/ask call failed: {}", e.getMessage());
            throw new IllegalStateException("Datalinks query failed", e);
        }
    }
}
