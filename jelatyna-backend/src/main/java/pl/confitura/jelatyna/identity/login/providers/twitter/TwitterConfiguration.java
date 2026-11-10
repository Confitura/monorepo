package pl.confitura.jelatyna.identity.login.providers.twitter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.identity.login.domain.OAuthUserService;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(TwitterConfigurationProperties.class)
class TwitterConfiguration {

    @Bean
    TwitterService twitterService(OAuthUserService oauthUserService, TwitterConfigurationProperties properties,
                                  ObjectMapper mapper) {
        return new TwitterService(oauthUserService, properties, mapper);
    }
}
