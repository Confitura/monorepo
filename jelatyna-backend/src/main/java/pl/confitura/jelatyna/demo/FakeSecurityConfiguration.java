package pl.confitura.jelatyna.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.identity.user.domain.TokenService;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

import static pl.confitura.jelatyna.platform.Profiles.FAKE_SECURITY;

@Configuration
@Profile(FAKE_SECURITY)
class FakeSecurityConfiguration {

    @Bean
    FakeLoginUsers fakeLoginUsers(UserRepository userRepository, PresentationRepository presentationRepository,
                                  EditionRepository editionRepository, TokenService tokenService) {
        return new FakeLoginUsers(userRepository, presentationRepository, editionRepository, tokenService);
    }
}
