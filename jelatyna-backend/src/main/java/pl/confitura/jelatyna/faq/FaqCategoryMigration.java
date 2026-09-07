package pl.confitura.jelatyna.faq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * One-off, idempotent backfill of the value→entity move for FAQ categories (see
 * docs/adr/0001). On the first boot after deploy it reads the legacy free-text
 * {@code faq_entry.category} column, creates one {@link FaqCategory} per distinct value
 * (published, ordered alphabetically so the pre-migration public order is preserved),
 * and points each entry at its category. Subsequent boots are no-ops.
 *
 * <p>Uses raw SQL on purpose: it must read a column the entity no longer maps, and it
 * runs at startup where there is no authenticated admin for the guarded repositories.
 */
@Slf4j
@Component
@RequiredArgsConstructor
class FaqCategoryMigration implements CommandLineRunner {

    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void run(String... args) {
        Integer categoryCount = jdbc.queryForObject("SELECT count(*) FROM faq_category", Integer.class);
        if (categoryCount != null && categoryCount > 0) {
            return; // already migrated
        }

        List<String> rawNames;
        try {
            rawNames = jdbc.queryForList(
                    "SELECT DISTINCT category FROM faq_entry WHERE category IS NOT NULL", String.class);
        } catch (DataAccessException e) {
            // Legacy column absent (fresh schema / nothing to migrate).
            return;
        }
        if (rawNames.isEmpty()) {
            return;
        }

        // Group the raw values by their trimmed, case-insensitive key; keep the first
        // trimmed spelling as the canonical display name.
        Map<String, String> keyToName = new LinkedHashMap<>();
        for (String raw : rawNames) {
            String name = raw.strip();
            if (!name.isEmpty()) {
                keyToName.putIfAbsent(name.toLowerCase(), name);
            }
        }

        // Assign displayOrder alphabetically (case-insensitive) to reproduce the old order.
        List<String> orderedKeys = keyToName.keySet().stream()
                .sorted(Comparator.comparing(keyToName::get, String.CASE_INSENSITIVE_ORDER))
                .toList();

        Map<String, String> keyToId = new LinkedHashMap<>();
        int order = 0;
        for (String key : orderedKeys) {
            String id = UUID.randomUUID().toString();
            jdbc.update("INSERT INTO faq_category (id, name, display_order, published) VALUES (?, ?, ?, ?)",
                    id, keyToName.get(key), order++, true);
            keyToId.put(key, id);
        }

        // Point each entry at its category, matching on the exact legacy string.
        for (String raw : rawNames) {
            String key = raw.strip().toLowerCase();
            String categoryId = keyToId.get(key);
            if (categoryId != null) {
                jdbc.update("UPDATE faq_entry SET category_id = ? WHERE category = ?", categoryId, raw);
            }
        }

        log.info("Migrated {} FAQ entries into {} categories", rawNames.size(), keyToId.size());
    }
}
