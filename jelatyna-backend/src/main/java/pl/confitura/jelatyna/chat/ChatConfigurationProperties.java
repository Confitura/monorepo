package pl.confitura.jelatyna.chat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("chat")
public class ChatConfigurationProperties {

    /** Master switch for the chat endpoint (also acts as the spend kill-switch). */
    private boolean enabled = false;

    /**
     * Optional shared secret. When set, callers must send it in the {@code X-Chat-Secret}
     * header or the request is rejected (401). Leave blank to make the endpoint open.
     * Note: a header secret is only private while callers are trusted — do not embed it
     * in a public browser bundle.
     */
    private String secret;

    /** Reject questions longer than this (characters). */
    private int maxQuestionLength = 500;

    /** Per-visitor request budget per rolling minute. */
    private int rateLimitPerMinute = 10;

    /** Global monthly cap on paid Datalinks calls; when reached the chat is disabled. */
    private int monthlyCallCap = 5000;

    /**
     * Guidance passed to Datalinks as {@code helperPrompt}: keeps answers on-topic,
     * replies in the visitor's language, and links to each record's {@code url}.
     */
    private String helperPrompt = """
            You are the assistant for the Confitura conference. Answer only questions about \
            Confitura — its talks, workshops, schedule, speakers, sponsors, tickets, venue, FAQ \
            and news. If a question is unrelated, politely say you can only help with Confitura. \
            Reply in the same language as the question. When you mention a talk, workshop, \
            sponsor, or page that has a `url` field, link to it with a Markdown link. \
            For any time related questions use "Europe/Warsaw" time zone""";

    private Datalinks datalinks = new Datalinks();

    @Data
    public static class Datalinks {
        private String baseUrl;
        private String token;
        private String username;
        private String namespace;
    }
}
