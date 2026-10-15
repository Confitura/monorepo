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

    long countByCategoryId(String categoryId);

    /** Moves every entry in {@code from} into {@code to} (used by category merge). */
    @PreAuthorize("@security.isAdmin()")
    @Modifying
    @Transactional
    @Query("update FaqEntry e set e.category = :to where e.category = :from")
    int moveEntries(@Param("from") FaqCategory from, @Param("to") FaqCategory to);

    @Query("select e from FaqEntry e left join e.category c order by c.displayOrder, e.displayOrder")
    List<FaqEntry> findAllOrdered();

    @Query("select e from FaqEntry e where e.published = true and e.category.published = true "
            + "order by e.category.displayOrder, e.displayOrder")
    List<FaqEntry> findPublishedOrdered();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);
}
