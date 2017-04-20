package pl.confitura.jelatyna.news;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestResource(path = "news")
@CrossOrigin
public interface NewsRepository extends Repository<News, Long> {
    @PreAuthorize("@security.isAdmin()")
    Page<News> findAll(Pageable pageable);

    @Query("FROM News as n WHERE n.published = true")
    @RestResource(path = "published", rel = "published")
    Page<News> findPublished(Pageable pageable);

}
