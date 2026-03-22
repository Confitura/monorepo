package pl.confitura.jelatyna.presentation.rating;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

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

        var rate = rateRepository.save(r);
        presentation.getRatings().add(rate);
        repository.save(presentation);
        return rate;
    }

    public void updateRating(Rate rate) {
        if (rate.getId() == null || !rateRepository.existsById(rate.getId())) {
            throw new EntityNotFoundException();
        }
        rateRepository.save(rate);
    }
}
