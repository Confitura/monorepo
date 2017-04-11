package pl.confitura.jelatyna.login.twitter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "twitter")
public class TwitterConfigurationProperties {
    private String apiKey;
    private String apiSecret;
    private String callback;

}
