package pl.confitura.jelatyna.presentation.rating;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RateRepository extends Repository<Rate, String> {

    Rate save(Rate rate);

    boolean existsById(String id);

    Optional<Rate> findByReviewerTokenAndPresentationId(String reviewerToken, String id);
}
