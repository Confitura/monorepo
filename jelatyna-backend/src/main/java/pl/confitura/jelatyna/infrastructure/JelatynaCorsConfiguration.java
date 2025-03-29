package pl.confitura.jelatyna.infrastructure;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
public class JelatynaCorsConfiguration {

    private final CorsConfigurationProperties properties;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        final CorsConfiguration config = buildCorsConfiguration();
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new FilterRegistrationBean<>(new CorsFilter(source));
    }

    private CorsConfiguration buildCorsConfiguration() {
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(properties.getOrigins());
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        return config;
    }

    @ConfigurationProperties("app.cors")
    @Value
    public static class CorsConfigurationProperties {
        List<String> origins;
    }
}
