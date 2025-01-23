package pl.confitura.jelatyna.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class JelatynaCorsConfiguration {

    @Bean
    public RepositoryRestConfigurer corsRepositoryRestConfigurer() {
        return RepositoryRestConfigurer.withConfig(
                (config, corsRegistry) -> corsRegistry.addMapping("/**")
        );

    }
}
