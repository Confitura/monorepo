package pl.confitura.jelatyna.login.google;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by tj on 12.06.17.
 */
@Data
@ConfigurationProperties(prefix = "google")
public class GoogleConfigurationProperties {
    private String apiKey;
    private String apiSecret;
    private String callback;
}
