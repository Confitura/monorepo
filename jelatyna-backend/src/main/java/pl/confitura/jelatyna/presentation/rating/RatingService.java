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
    private UsersPerformedRateRepository usersPerformedRateRepository;

    @Transactional
    public Rate rate(String presentationId, String reviewerToken,  Rate rate) {
        verifyPresentationNotRatedByReviewer(presentationId, reviewerToken);
        Presentation presentation = repository.findById(presentationId);
        markUserRated(presentation, reviewerToken);
        rate = saveRate(rate, presentation);
        return rate;
    }

    private void verifyPresentationNotRatedByReviewer(String presentationId, String reviewerToken) {
        if(usersPerformedRateRepository.existsByReviewerTokenAndPresentationId(reviewerToken, presentationId)){
            throw new UserAlreadyRatedPresentation(reviewerToken, presentationId);
        }
    }

    private Rate saveRate(Rate rate, Presentation presentation) {
        rate = rateRepository.save(rate);
        presentation.getRatings().add(rate);
        repository.save(presentation);
        return rate;
    }

    private void markUserRated(Presentation presentation, String reviewerToken) {
        usersPerformedRateRepository.save(new UsersPerformedRate(reviewerToken, presentation));
    }

    public void updateRating(Rate rate) {
        if(rate.getId() == null || !rateRepository.existsById(rate.getId())){
            throw new EntityNotFoundException();
        }
        rateRepository.save(rate);
    }
}
