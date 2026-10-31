package pl.confitura.jelatyna.conference.rating.domain;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RateRepository extends Repository<Rate, String> {

    Rate save(Rate rate);

    Optional<Rate> findByReviewerTokenAndPresentationId(String reviewerToken, String id);
}
