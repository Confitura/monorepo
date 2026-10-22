package pl.confitura.jelatyna.presentation.rating;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ViewPresentationRateRepository extends Repository<ViewPresentationRate, String> {
    Optional<ViewPresentationRate> findByPresentationId(String presentationId);

    // The view's own ORDER BY is not guaranteed to survive the filter, so the query repeats it.
    @Query("SELECT r FROM ViewPresentationRate r " +
           "WHERE r.presentationId IN (SELECT p.id FROM Presentation p WHERE p.edition.current = true) " +
           "ORDER BY r.avgRating DESC")
    List<ViewPresentationRate> findAllInCurrentEdition();
}
