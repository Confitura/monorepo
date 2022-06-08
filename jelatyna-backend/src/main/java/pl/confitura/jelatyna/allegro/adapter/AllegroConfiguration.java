package pl.confitura.jelatyna.allegro.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

@Configuration
@EnableConfigurationProperties(AllegroProperties.class)
@RequiredArgsConstructor
class AllegroConfiguration {
    private final AllegroProperties properties;
    private final ObjectMapper objectMapper;

    @Bean
    @Scope(scopeName = SCOPE_SESSION, proxyMode = TARGET_CLASS)
    AllegroClient allegroClient() {
        return new AllegroClient(properties, objectMapper);
    }

}
