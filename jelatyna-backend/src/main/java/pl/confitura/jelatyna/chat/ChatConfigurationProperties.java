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

    private Datalinks datalinks = new Datalinks();

    @Data
    public static class Datalinks {
        private String baseUrl;
        private String token;
        private String username;
        private String namespace;
    }
}
