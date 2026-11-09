package pl.confitura.jelatyna.platform.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ResourceConfigurationProperties.class)
class StorageConfiguration {

    @Bean
    ResourceStorage resourceStorage(ResourceConfigurationProperties properties) {
        return new ResourceStorage(properties);
    }
}
