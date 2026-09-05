package pl.confitura.jelatyna.faq;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface FaqEntryRepository extends Repository<FaqEntry, String> {

    @PreAuthorize("@security.isAdmin()")
    FaqEntry save(FaqEntry entry);

    FaqEntry findById(String id);

    @Query("select e from FaqEntry e order by e.category, e.displayOrder")
    List<FaqEntry> findAllOrdered();

    @Query("select e from FaqEntry e where e.published = true order by e.category, e.displayOrder")
    List<FaqEntry> findPublishedOrdered();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);
}
