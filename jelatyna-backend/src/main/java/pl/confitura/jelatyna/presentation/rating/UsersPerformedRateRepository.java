package pl.confitura.jelatyna.presentation.rating;

import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersPerformedRateRepository extends org.springframework.data.repository.Repository<UsersPerformedRate, String> {

    @RestResource(exported = false)
    UsersPerformedRate save(UsersPerformedRate rate);

    boolean existsByUserIdAndPresentationId(String userId, String presentationId);
}
