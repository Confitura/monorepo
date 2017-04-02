package pl.confitura.jelatyna.presentation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "presentations")
public interface PresentationRepository extends JpaRepository<Presentation, String>{
}
