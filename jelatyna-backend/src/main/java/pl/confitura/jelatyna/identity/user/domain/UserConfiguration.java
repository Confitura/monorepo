package pl.confitura.jelatyna.identity.user.domain;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TokenService.JwtProperties.class)
class UserConfiguration {

    // Named "security" for the @security checks in @PreAuthorize.
    @Bean
    Security security(UserRepository userRepository) {
        return new Security(userRepository);
    }

    @Bean
    TokenService tokenService(TokenService.JwtProperties properties, Speakers speakers) {
        return new TokenService(properties, speakers);
    }
}
