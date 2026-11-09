package pl.confitura.jelatyna.identity.user.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.identity.user.domain.TokenService;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;
import tools.jackson.databind.ObjectMapper;

@Configuration
class UserWebConfiguration {

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(TokenService tokenService, UserRepository userRepository,
                                                    ObjectMapper mapper) {
        return new JwtAuthenticationFilter(tokenService, userRepository, mapper);
    }
}
