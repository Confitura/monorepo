package pl.confitura.jelatyna.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@Configuration
public class JelatynaCorsConfiguration {

    @Value("${ui.origin:*}")
    private String origin;

    @Bean
    public RepositoryRestConfigurerAdapter repositoryRestConfigurerAdapter() {
        return new RepositoryRestConfigurerAdapter() {
            @Override
            public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
                config
                        .getCorsRegistry()
                        .addMapping("/**")
                        .allowedOrigins(origin);
            }
        };
    }

}
