package pl.confitura.jelatyna.presentation.rating;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.user.UserFacade;
import pl.confitura.jelatyna.user.dto.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@AllArgsConstructor
public class RatingService {

    private RateRepository rateRepository;
    private UsersPerformedRateRepository usersPerformedRateRepository;
    private UserFacade userFacade;
    private Security security;

    @Transactional
    public Rate rate(String presentationId, Rate rate) {
        String userId = security.getUserId();
        verifyPresentationNotRatedByUser(presentationId, userId);

        User user = userFacade.findById(userId);
        markUserRated(presentationId, user);
        rate = saveRate(rate, presentationId);
        return rate;
    }

    private void verifyPresentationNotRatedByUser(String presentationId, String userId) {
        if(usersPerformedRateRepository.existsByUserIdAndPresentationId(userId, presentationId)){
            throw new UserAlreadyRatedPresentation(userId, presentationId);
        }
    }

    private Rate saveRate(Rate rate, String presentationId) {
        rate.setPresentationId(presentationId);
        rate = rateRepository.save(rate);
        return rate;
    }

    private void markUserRated(String presentationId, User user) {
        usersPerformedRateRepository.save(new UsersPerformedRate(user, presentationId));
    }

    void updateRating(Rate rate) {
        if(rate.getId() == null || !rateRepository.existsById(rate.getId())){
            throw new EntityNotFoundException();
        }
        rateRepository.save(rate);
    }

    public List<Rate> findForPresentation(String presentationId) {
        return rateRepository.findAllByPresentationId(presentationId);
    }
}
