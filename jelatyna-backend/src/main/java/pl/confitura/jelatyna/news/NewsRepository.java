package pl.confitura.jelatyna.news;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
public interface NewsRepository extends Repository<News, Long> {
    @PreAuthorize("@security.isAdmin()")
    Page<News> findAll(Pageable pageable);

    @Query("FROM News as n WHERE n.published = true")
    Page<News> findPublished(Pageable pageable);

}
