package pl.confitura.jelatyna.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.ConferenceConfigurationProperties;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.user.UserFacade;

@RequiredArgsConstructor
@Configuration
class PresentationConfiguration {

    private final PresentationRepository repository;
    private final UserFacade userFacade;
    private final ConferenceConfigurationProperties conferenceConfiguration;
    private final Security security;

    @Bean
    PresentationFacade presentationFacade(){
        return new PresentationFacade(repository, userFacade, conferenceConfiguration, security);
    }
}
