package pl.confitura.jelatyna.conference.rating.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.conference.agenda.domain.DayRepository;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;

@Configuration
class RatingConfiguration {

    @Bean
    RatingService ratingService(PresentationRepository repository, RateRepository rateRepository) {
        return new RatingService(repository, rateRepository);
    }

    @Bean
    RatingWindowService ratingWindowService(DayRepository dayRepository) {
        return new RatingWindowService(dayRepository);
    }
}
