package pl.confitura.jelatyna.presentation.rating;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "ratings")
public interface RateRepository extends Repository<Rate, String> {

    @RestResource(exported = false)
    Rate save(Rate rate);

    boolean existsById(String id);

    List<Rate> findAllByPresentationId(String presentationId);
}
