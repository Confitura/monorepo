package pl.confitura.jelatyna.chat;

import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.chat.ChatTypes.AskRequest;
import pl.confitura.jelatyna.chat.ChatTypes.EventSink;
import pl.confitura.jelatyna.chat.ChatTypes.SseEvent;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

/**
 * Orchestrates a chat question: pre-flight controls, bidirectional speaker
 * name↔id translation, the Datalinks call, caching, and relaying SSE events.
 * The Datalinks token lives only in {@link DatalinksClient} and never reaches a sink.
 */
@Service
public class ChatService {

    private final ChatConfigurationProperties properties;
    private final DatalinksClient datalinksClient;
    private final SpeakerDirectory speakerDirectory;
    private final RateLimiter rateLimiter;
    private final MonthlyGate monthlyGate;
    private final AnswerCache answerCache;
    private final ObjectMapper objectMapper;

    public ChatService(ChatConfigurationProperties properties,
                       DatalinksClient datalinksClient,
                       SpeakerDirectory speakerDirectory,
                       RateLimiter rateLimiter,
                       MonthlyGate monthlyGate,
                       AnswerCache answerCache,
                       ObjectMapper objectMapper) {
        this.properties = properties;
        this.datalinksClient = datalinksClient;
        this.speakerDirectory = speakerDirectory;
        this.rateLimiter = rateLimiter;
        this.monthlyGate = monthlyGate;
        this.answerCache = answerCache;
        this.objectMapper = objectMapper;
    }

    /**
     * Rejects the request when a shared secret is configured and the supplied header
     * does not match it (constant-time compare). No-op when no secret is configured.
     */
    public void authorize(String providedSecret) {
        String secret = properties.getSecret();
        if (secret == null || secret.isBlank()) {
            return;
        }
        byte[] expected = secret.getBytes(StandardCharsets.UTF_8);
        byte[] actual = (providedSecret == null ? "" : providedSecret).getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, actual)) {
            throw new ChatException(ChatException.Reason.UNAUTHORIZED, "Invalid or missing secret");
        }
    }

    /** Synchronous checks that must happen before streaming starts. Throws {@link ChatException}. */
    public void preflight(AskRequest request, String clientKey) {
        if (!properties.isEnabled()) {
            throw new ChatException(ChatException.Reason.DISABLED, "Chat is disabled");
        }
        String question = request == null ? null : request.question();
        if (question == null || question.isBlank()) {
            throw new ChatException(ChatException.Reason.INVALID, "Question is required");
        }
        if (question.length() > properties.getMaxQuestionLength()) {
            throw new ChatException(ChatException.Reason.TOO_LONG, "Question is too long");
        }
        if (!rateLimiter.tryAcquire(clientKey)) {
            throw new ChatException(ChatException.Reason.RATE_LIMITED, "Too many requests");
        }
        if (monthlyGate.isExhausted()) {
            throw new ChatException(ChatException.Reason.CAP_REACHED, "Monthly limit reached");
        }
    }

    /** Streams the answer to {@code sink}. Assumes {@link #preflight} already passed. */
    public void stream(AskRequest request, EventSink sink) throws Exception {
        String question = request.question();
        boolean cacheable = request.conversationId() == null || request.conversationId().isBlank();

        if (cacheable) {
            Optional<String> cached = answerCache.get(question);
            if (cached.isPresent()) {
                sink.send(new SseEvent("answer", cached.get()));
                return;
            }
        }

        monthlyGate.consume();
        SpeakerTranslator translator = new SpeakerTranslator(speakerDirectory.idToName());
        String query = translator.namesToIds(question);

        // Datalinks emits one terminal "answer" event carrying the full response
        // (AutoRagAnswerEvent { response }); each answer event therefore replaces,
        // matching the widget's render, so the cache holds what the user saw.
        StringBuilder lastAnswer = new StringBuilder();
        datalinksClient.ask(query, request.conversationId(), event -> {
            try {
                if ("answer".equals(event.event())) {
                    String translated = translateAnswerData(event.data(), translator);
                    lastAnswer.setLength(0);
                    lastAnswer.append(translated);
                    sink.send(new SseEvent("answer", translated));
                } else {
                    sink.send(event);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        if (cacheable && lastAnswer.length() > 0) {
            answerCache.put(question, lastAnswer.toString());
        }
    }

    /** Translates speaker ids to names inside the {@code response} field of an answer event. */
    private String translateAnswerData(String data, SpeakerTranslator translator) {
        try {
            ObjectNode node = (ObjectNode) objectMapper.readTree(data);
            if (node.has("response")) {
                node.put("response", translator.idsToNames(node.get("response").asString()));
            }
            return node.toString();
        } catch (Exception e) {
            // Not the expected JSON shape — translate the raw text as a fallback.
            return translator.idsToNames(data);
        }
    }
}
