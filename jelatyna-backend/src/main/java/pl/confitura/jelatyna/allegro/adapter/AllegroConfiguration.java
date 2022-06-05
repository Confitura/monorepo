package pl.confitura.jelatyna.allegro.adapter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

@Configuration
@EnableConfigurationProperties(AllegroProperties.class)
class AllegroConfiguration {

    @Bean
    AllegroClient allegroClient(AllegroProperties properties) {
        return new AllegroClient(properties);
    }

    @Bean
    @Scope(scopeName = SCOPE_SESSION)
    AllegroAuthorizationContext authorizationContext() {
        return new AllegroAuthorizationContext();
    }

}
