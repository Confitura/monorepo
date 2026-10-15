package pl.confitura.jelatyna.faq;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface FaqCategoryRepository extends Repository<FaqCategory, String> {

    @PreAuthorize("@security.isAdmin()")
    FaqCategory save(FaqCategory category);

    FaqCategory findById(String id);

    /** Case-insensitive lookup used to resolve-or-create a category from an entry's name. */
    Optional<FaqCategory> findByNameIgnoreCase(String name);

    List<FaqCategory> findAllByOrderByDisplayOrderAsc();

    long count();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);
}
