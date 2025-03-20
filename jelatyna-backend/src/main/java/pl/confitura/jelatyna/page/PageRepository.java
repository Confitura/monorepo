package pl.confitura.jelatyna.page;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RestResource(path = "pages")
public interface PageRepository extends Repository<Page, String> {
    Page findById(String id);

    @PreAuthorize("@security.isAdmin()")
    Page save(Page page);
}
