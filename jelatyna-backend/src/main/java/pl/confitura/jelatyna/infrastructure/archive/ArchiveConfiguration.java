package pl.confitura.jelatyna.infrastructure.archive;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.confitura.jelatyna.resource.ResourceConfigurationProperties;
import pl.confitura.jelatyna.user.UserController;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class ArchiveConfiguration {

    private final ObjectMapper objectMapper;
    private final UserController userController;
    private final ResourceConfigurationProperties resourceConfigurationProperties;

    @Bean
    WebpageDataDumper webpageDataDumper() {
        return new WebpageDataDumper(
                objectMapper,
                userController,
                resourceConfigurationProperties.folder() + "/edition-2025"
        );
    }
}
