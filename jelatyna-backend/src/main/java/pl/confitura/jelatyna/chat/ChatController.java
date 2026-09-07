package pl.confitura.jelatyna.chat;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.confitura.jelatyna.chat.ChatTypes.AskRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/** Public chat endpoint: relays the Datalinks AutoRAG stream as SSE, with speaker names rejoined. */
@Slf4j
@RestController
public class ChatController {

    private static final long TIMEOUT_MS = Duration.ofMinutes(2).toMillis();

    private final ChatService chatService;
    private final ExecutorService chatExecutor;

    public ChatController(ChatService chatService, ExecutorService chatExecutor) {
        this.chatService = chatService;
        this.chatExecutor = chatExecutor;
    }

    @PostMapping(value = "/chat/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter ask(@RequestBody AskRequest request, HttpServletRequest httpRequest) {
        // Auth + pre-flight in the request thread so failures map to an HTTP status,
        // before any streaming begins and without calling Datalinks.
        chatService.authorize(httpRequest.getHeader("X-Chat-Secret"));
        chatService.preflight(request, clientKey(httpRequest));

        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        chatExecutor.execute(() -> {
            try {
                chatService.stream(request, event -> emitter.send(utf8Event(event.event(), event.data())));
                emitter.complete();
            } catch (Exception e) {
                log.warn("Chat stream failed: {}", e.getMessage());
                try {
                    emitter.send(utf8Event("error", "{\"message\":\"chat failed\"}"));
                } catch (IOException ignored) {
                    // client already gone
                }
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    /**
     * SSE data as pre-encoded UTF-8 bytes. Passing a String lets the default String
     * message converter encode it as ISO-8859-1, which mangles non-ASCII (Polish
     * characters); a byte[] is written verbatim, and browsers decode SSE as UTF-8.
     */
    private static SseEmitter.SseEventBuilder utf8Event(String name, String data) {
        return SseEmitter.event()
                .name(name)
                .data(data.getBytes(StandardCharsets.UTF_8), MediaType.APPLICATION_OCTET_STREAM);
    }

    @ExceptionHandler(ChatException.class)
    ResponseEntity<Map<String, String>> handle(ChatException e) {
        return ResponseEntity.status(e.reason().status())
                .body(Map.of("error", e.reason().name(), "message", e.getMessage()));
    }

    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
