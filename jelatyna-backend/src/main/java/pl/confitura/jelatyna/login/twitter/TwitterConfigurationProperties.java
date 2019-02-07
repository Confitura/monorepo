package pl.confitura.jelatyna.login.twitter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "twitter")
public class TwitterConfigurationProperties {
    private String apiKey;
    private String apiSecret;
    private String callback;
    private String accessToken;
    private String accessTokenSecret;

}
