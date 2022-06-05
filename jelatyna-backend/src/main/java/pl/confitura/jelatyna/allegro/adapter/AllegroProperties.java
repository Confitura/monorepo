package pl.confitura.jelatyna.allegro.adapter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("allegro")
class AllegroProperties {
    private String clientId;
    private String clientSecret;

    public AllegroProperties() {
    }

    public AllegroProperties(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
}
