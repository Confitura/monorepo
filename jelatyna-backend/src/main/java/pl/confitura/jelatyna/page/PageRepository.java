package pl.confitura.jelatyna.page;

import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PageRepository extends Repository<Page, String> {
    Page findById(String id);

    @PreAuthorize("@security.isAdmin()")
    Page save(Page page);
}
