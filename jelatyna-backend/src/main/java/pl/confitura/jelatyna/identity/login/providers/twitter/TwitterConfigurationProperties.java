package pl.confitura.jelatyna.identity.login.providers.twitter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "twitter")
public class TwitterConfigurationProperties {
    private String apiKey;
    private String apiSecret;
    private String callback;

}
