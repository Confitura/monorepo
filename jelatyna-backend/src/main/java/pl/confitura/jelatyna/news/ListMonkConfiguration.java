package pl.confitura.jelatyna.news;

import com.eventuallycoding.listmonk.ListmonkClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ListMonkConfiguration {

    private final ListMonkConfigurationProperties properties;

    ListmonkClient listmonkClient() {
        return new ListmonkClient(
                properties.baseUrl(),
                properties.username(),
                properties.password(),
                false,
                60L,
                60L
        );
    }

    @Bean
    ListMonk listMonk() {
        return new ListMonk(listmonkClient());
    }

    @ConfigurationProperties(prefix = "listmonk")
    public record ListMonkConfigurationProperties(
            String baseUrl,
            String username,
            String password) {

    }
}
