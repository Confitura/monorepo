package pl.confitura.jelatyna.presentation.rating;

import org.springframework.data.repository.Repository;

public interface RateRepository extends Repository<Rate, String> {

    Rate save(Rate rate);

    boolean existsById(String id);
}
