package pl.confitura.jelatyna.presentation.rating;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface RateRepository extends Repository<Rate, String> {

    Rate save(Rate rate);

    boolean existsById(String id);

    @Query("select p.ratings from pl.confitura.jelatyna.presentation.Presentation p where p.id = ?1")
    List<Rate> findByPresentationId(String presentationId);

    List<Rate> findAll();
}
