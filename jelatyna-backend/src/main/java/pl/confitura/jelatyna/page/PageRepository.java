package pl.confitura.jelatyna.page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "pages")
public interface PageRepository extends JpaRepository<Page, String> {
}
