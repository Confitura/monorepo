package pl.confitura.jelatyna.webpage.chat.datalinks;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.webpage.chat.domain.ChatConfigurationProperties;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(ChatConfigurationProperties.class)
class DatalinksConfiguration {

    @Bean
    HttpDatalinksClient httpDatalinksClient(ChatConfigurationProperties properties, ObjectMapper objectMapper) {
        return new HttpDatalinksClient(properties, objectMapper);
    }
}
