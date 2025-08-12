package pl.confitura.jelatyna.news;

import com.eventuallycoding.listmonk.ListmonkClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
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
    NewsletterApi listMonk() {
        return new ListmonkApi(listmonkClient());
    }

    @ConfigurationProperties(prefix = "listmonk")
    public record ListMonkConfigurationProperties(
            String baseUrl,
            String username,
            String password) {

    }
}
