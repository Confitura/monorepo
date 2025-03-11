package pl.confitura.jelatyna.presentation.rating;

import org.springframework.stereotype.Repository;

@Repository
public interface UsersPerformedRateRepository extends org.springframework.data.repository.Repository<UsersPerformedRate, String> {

    UsersPerformedRate save(UsersPerformedRate rate);

    boolean existsByReviewerTokenAndPresentationId(String reviewerToken, String presentationId);
}
