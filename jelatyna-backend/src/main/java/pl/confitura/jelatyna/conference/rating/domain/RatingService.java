package pl.confitura.jelatyna.conference.rating.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;

@Service
@AllArgsConstructor
public class RatingService {

    private PresentationRepository repository;
    private RateRepository rateRepository;

    @Transactional
    public Rate rate(String presentationId, Rate rate) {
        Presentation presentation = repository.findById(presentationId);
        rate.setPresentation(presentation);
        rate = saveRate(rate, presentation);
        return rate;
    }

    private Rate saveRate(final Rate newRate, Presentation presentation) {
        var r = rateRepository.findByReviewerTokenAndPresentationId(newRate.getReviewerToken(), presentation.getId())
                .map(it -> it.update(newRate))
                .orElse(newRate);

        return rateRepository.save(r);
    }
}
