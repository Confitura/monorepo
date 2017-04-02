package pl.confitura.jelatyna.presentation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//@RestResource(path = "presentations")
@RepositoryRestResource(path = "presentations", excerptProjection = InlineTags.class)
public interface PresentationRepository extends JpaRepository<Presentation, String>{
}
