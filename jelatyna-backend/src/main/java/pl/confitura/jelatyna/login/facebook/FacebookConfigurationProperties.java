package pl.confitura.jelatyna.login.facebook;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "facebook")
public class FacebookConfigurationProperties {
    private String apiKey;
    private String apiSecret;
    private String callback;

}
