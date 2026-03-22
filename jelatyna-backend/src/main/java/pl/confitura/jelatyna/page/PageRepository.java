package pl.confitura.jelatyna.page;

import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends Repository<Page, String> {

    List<Page> findAll();

    Optional<Page> findById(String id);

    @PreAuthorize("@security.isAdmin()")
    Page save(Page page);

    void deleteById(String id);
}
