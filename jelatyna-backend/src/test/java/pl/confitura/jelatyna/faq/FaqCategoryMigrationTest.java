package pl.confitura.jelatyna.faq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FaqCategoryMigrationTest extends BaseIntegrationTest {

    @Autowired
    private FaqCategoryMigration migration;

    @Autowired
    private FaqEntryRepository entryRepository;

    @Autowired
    private FaqCategoryRepository categoryRepository;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private TransactionTemplate txTemplate;

    @BeforeEach
    public void setUp() {
        SecurityHelper.asAdmin();
        txTemplate.executeWithoutResult(status -> {
            entryRepository.findAllOrdered().forEach(e -> entryRepository.deleteById(e.getId()));
            categoryRepository.findAllByOrderByDisplayOrderAsc()
                    .forEach(c -> categoryRepository.deleteById(c.getId()));
        });
        // Recreate the legacy free-text column that the entity no longer maps, and seed
        // rows exactly as they would look before the value→entity cutover.
        jdbc.execute("ALTER TABLE faq_entry ADD COLUMN IF NOT EXISTS category VARCHAR(255)");
        seedLegacyEntry("Venue", 0);
        seedLegacyEntry("General", 1);
        seedLegacyEntry("general", 2); // same category, different casing/spacing
    }

    private void seedLegacyEntry(String category, int order) {
        jdbc.update("INSERT INTO faq_entry (id, category, question, answer, display_order, published) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                UUID.randomUUID().toString(), category, "Q" + order, "A" + order, order, true);
    }

    @Test
    void backfillsCategoriesAlphabeticallyAndDedupesCaseInsensitively() {
        migration.run();

        var categories = categoryRepository.findAllByOrderByDisplayOrderAsc();
        // "general"/"General" collapse to one; ordered alphabetically → General(0), Venue(1).
        assertThat(categories).extracting(FaqCategory::getName).containsExactly("General", "Venue");
        assertThat(categories).allMatch(FaqCategory::isPublished);

        // Every entry now points at a category matching its old string (case-insensitively).
        List<FaqEntry> entries = entryRepository.findAllOrdered();
        assertThat(entries).hasSize(3);
        assertThat(entries).allMatch(e -> e.getCategory() != null);
    }

    @Test
    void isIdempotent() {
        migration.run();
        long afterFirst = categoryRepository.count();
        migration.run();
        assertThat(categoryRepository.count()).isEqualTo(afterFirst);
    }
}
