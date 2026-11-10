package pl.confitura.jelatyna.identity.login.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.identity.user.domain.LoginRepository;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

@Configuration
class LoginConfiguration {

    // The name a scanned OAuthUserService had: JavaBeans leave a name starting with two capitals as it is.
    @Bean("OAuthUserService")
    OAuthUserService oauthUserService(UserRepository userRepository, LoginRepository loginRepository) {
        return new OAuthUserService(userRepository, loginRepository);
    }
}
