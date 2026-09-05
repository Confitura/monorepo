package pl.confitura.jelatyna.faq;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FaqEntryRepository extends Repository<FaqEntry, String> {

    @PreAuthorize("@security.isAdmin()")
    FaqEntry save(FaqEntry entry);

    FaqEntry findById(String id);

    /** Bulk-renames a category across every entry that has it. Returns rows updated. */
    @PreAuthorize("@security.isAdmin()")
    @Modifying
    @Transactional
    @Query("update FaqEntry e set e.category = :to where e.category = :from")
    int renameCategory(@Param("from") String from, @Param("to") String to);

    @Query("select e from FaqEntry e order by e.category, e.displayOrder")
    List<FaqEntry> findAllOrdered();

    @Query("select e from FaqEntry e where e.published = true order by e.category, e.displayOrder")
    List<FaqEntry> findPublishedOrdered();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);
}
