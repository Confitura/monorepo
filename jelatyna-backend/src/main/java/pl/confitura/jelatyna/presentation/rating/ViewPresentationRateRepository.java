package pl.confitura.jelatyna.presentation.rating;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ViewPresentationRateRepository extends Repository<ViewPresentationRate, String> {
    Optional<ViewPresentationRate> findByPresentationId(String presentationId);

    List<ViewPresentationRate> findAll();
}
