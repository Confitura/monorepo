package pl.confitura.jelatyna.presentation.rating;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.dto.FullUserDto;
import pl.confitura.jelatyna.user.UserFacade;

@Service
@AllArgsConstructor
public class RatingService {

    private PresentationRepository repository;
    private RateRepository rateRepository;
    private UsersPerformedRateRepository usersPerformedRateRepository;
    private UserFacade userFacade;
    private Security security;

    @Transactional
    public Rate rate(String presentationId, Rate rate) {
        String userId = security.getUserId();
        verifyPresentationNotRatedByUser(presentationId, userId);

        Presentation presentation = repository.findById(presentationId);
        FullUserDto user = userFacade.findById(userId);
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

    private void markUserRated(Presentation presentation, FullUserDto user) {
        usersPerformedRateRepository.save(new UsersPerformedRate(user, presentation));
    }

    public void updateRating(Rate rate) {
        if(rate.getId() == null || !rateRepository.existsById(rate.getId())){
            throw new EntityNotFoundException();
        }
        rateRepository.save(rate);
    }
}
