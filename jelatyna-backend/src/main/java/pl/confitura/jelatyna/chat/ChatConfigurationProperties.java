package pl.confitura.jelatyna.chat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("chat")
public class ChatConfigurationProperties {

    /** Master switch for the chat endpoint (also acts as the spend kill-switch). */
    private boolean enabled = false;

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
