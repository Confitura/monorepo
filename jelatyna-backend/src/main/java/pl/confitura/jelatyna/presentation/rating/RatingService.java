package pl.confitura.jelatyna.presentation.rating;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import javax.persistence.EntityNotFoundException;

@Service
@AllArgsConstructor
public class RatingService {

    private PresentationRepository repository;
    private RateRepository rateRepository;
    private UsersPerformedRateRepository usersPerformedRateRepository;
    private UserRepository userRepository;
    private Security security;

    @Transactional
    public Rate rate(String presentationId, Rate rate) {
        String userId = security.getUserId();
        verifyPresentationNotRatedByUser(presentationId, userId);

        Presentation presentation = repository.findById(presentationId);
        User user = userRepository.findById(userId);
        if (!user.hasArrived()) {
            throw new UserRatingPresentationHaveNotArrived();
        }

        markUserRated(presentation, user);
        rate = saveRate(rate, presentation);
        return rate;
    }

    private void verifyPresentationNotRatedByUser(String presentationId, String userId) {
        if(usersPerformedRateRepository.existsByUserIdAndPresentationId(userId, presentationId)){
            throw new UserAlreadyRatedPresentation(userId, presentationId);
        }
    }

    private Rate saveRate(Rate rate, Presentation presentation) {
        rate = rateRepository.save(rate);
        presentation.getRatings().add(rate);
        repository.save(presentation);
        return rate;
    }

    private void markUserRated(Presentation presentation, User user) {
        usersPerformedRateRepository.save(new UsersPerformedRate(user, presentation));
    }

    public void updateRating(Rate rate) {
        if(rate.getId() == null || !rateRepository.existsById(rate.getId())){
            throw new EntityNotFoundException();
        }
        rateRepository.save(rate);
    }
}
