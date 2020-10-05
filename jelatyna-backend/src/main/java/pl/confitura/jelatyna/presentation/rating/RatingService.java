package pl.confitura.jelatyna.presentation.rating;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.presentation.PresentationFacade;
import pl.confitura.jelatyna.user.UserFacade;
import pl.confitura.jelatyna.user.dto.FullUserDto;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@AllArgsConstructor
public class RatingService {

    private final RateRepository rateRepository;
    private final UsersPerformedRateRepository usersPerformedRateRepository;
    private final UserFacade userFacade;
    private final Security security;

    @Transactional
    @PreAuthorize("@security.isAuthenticated()")
    public Rate rate(Rate rate) {
        String userId = security.getUserId();
        verifyPresentationNotRatedByUser(rate.getPresentationId(), userId);

        FullUserDto user = userFacade.findById(userId);
        markUserRated(rate.getPresentationId(), user);
        rate = saveRate(rate);
        return rate;
    }

    private void verifyPresentationNotRatedByUser(String presentationId, String userId) {
        if (usersPerformedRateRepository.existsByUserIdAndPresentationId(userId, presentationId)) {
            throw new UserAlreadyRatedPresentation(userId, presentationId);
        }
    }

    private Rate saveRate(Rate rate) {
        rate = rateRepository.save(rate);
        return rate;
    }

    private void markUserRated(String presentationId, FullUserDto user) {
        usersPerformedRateRepository.save(new UsersPerformedRate(user, presentationId));
    }

    @PreAuthorize("@security.isAuthenticated()")
    public void updateRating(Rate rate) {
        if (rate.getId() == null || !rateRepository.existsById(rate.getId())) {
            throw new EntityNotFoundException();
        }
        rateRepository.save(rate);
    }

    public List<Rate> findRates(String presentationId) {
        return rateRepository.findAllByPresentationId(presentationId);
    }
}
