package pl.confitura.jelatyna.conference.edition.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;

@Configuration
class EditionWebConfiguration {

    @Bean
    ConferenceInfoContributor conferenceInfoContributor(EditionRepository editionRepository) {
        return new ConferenceInfoContributor(editionRepository);
    }
}
