package pl.confitura.jelatyna.allegro.adapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("allegro")
@Data @NoArgsConstructor
@Builder @AllArgsConstructor
class AllegroProperties {
    private String clientId;
    private String clientSecret;
    private String callback;
    private String api = "https://api.allegro.pl.allegrosandbox.pl";
    private String uri = "https://allegro.pl.allegrosandbox.pl";

}
