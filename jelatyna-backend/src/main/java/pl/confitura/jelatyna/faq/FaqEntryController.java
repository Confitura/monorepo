package pl.confitura.jelatyna.faq;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.confitura.jelatyna.page.PageController;

import java.util.List;
import java.util.Map;

/**
 * Manages FAQ questions individually. Public reads expose only published entries;
 * writes are admin-only (guarded on the repository's save/deleteById).
 */
@RestController
@RequestMapping("/faq-entries")
@RequiredArgsConstructor
public class FaqEntryController {

    private final FaqEntryRepository repository;
    private final PageController pageController;
    private final FaqMarkdownParser parser = new FaqMarkdownParser();

    /** Public: published entries, grouped-ready (ordered by category then displayOrder). */
    @GetMapping
    public List<FaqEntryDto> getPublishedFaqEntries() {
        return repository.findPublishedOrdered().stream().map(FaqEntryDto::from).toList();
    }

    /** Admin: all entries including unpublished, for the management screen. */
    @GetMapping("/all")
    @PreAuthorize("@security.isAdmin()")
    public List<FaqEntryDto> getAllFaqEntries() {
        return repository.findAllOrdered().stream().map(FaqEntryDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaqEntryDto> getFaqEntry(@PathVariable String id) {
        FaqEntry entry = repository.findById(id);
        return entry == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(FaqEntryDto.from(entry));
    }

    @PostMapping
    public ResponseEntity<FaqEntryDto> createFaqEntry(@RequestBody FaqEntryRequest request) {
        FaqEntry entry = new FaqEntry()
                .setCategory(request.category())
                .setQuestion(request.question())
                .setAnswer(request.answer())
                .setDisplayOrder(request.displayOrder() == null ? 0 : request.displayOrder())
                .setPublished(Boolean.TRUE.equals(request.published()));
        return ResponseEntity.status(HttpStatus.CREATED).body(FaqEntryDto.from(repository.save(entry)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FaqEntryDto> updateFaqEntry(@PathVariable String id, @RequestBody FaqEntryRequest request) {
        FaqEntry entry = repository.findById(id);
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }
        if (request.category() != null) entry.setCategory(request.category());
        if (request.question() != null) entry.setQuestion(request.question());
        if (request.answer() != null) entry.setAnswer(request.answer());
        if (request.displayOrder() != null) entry.setDisplayOrder(request.displayOrder());
        if (request.published() != null) entry.setPublished(request.published());
        return ResponseEntity.ok(FaqEntryDto.from(repository.save(entry)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaqEntry(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * One-off migration: seeds entries from the legacy single-blob `faq` page.
     * No-op (409) if entries already exist, so it is safe to run more than once.
     * The old `faq` page is only read, never modified.
     */
    @PostMapping("/import")
    @PreAuthorize("@security.isAdmin()")
    public ResponseEntity<Map<String, Integer>> importFromFaqPage() {
        if (!repository.findAllOrdered().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("imported", 0));
        }
        String content = pageController.getPage("faq").getBody();
        var parsed = parser.parse(content);
        int order = 0;
        for (FaqMarkdownParser.ParsedFaqEntry p : parsed) {
            repository.save(new FaqEntry()
                    .setCategory(p.category())
                    .setQuestion(p.question())
                    .setAnswer(p.answer())
                    .setDisplayOrder(order++)
                    .setPublished(true));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imported", parsed.size()));
    }

    /** Persists a new order: each id's displayOrder becomes its index in the list. */
    @PutMapping("/order")
    public ResponseEntity<Void> reorderFaqEntries(@RequestBody ReorderRequest request) {
        int order = 0;
        for (String id : request.ids()) {
            FaqEntry entry = repository.findById(id);
            if (entry != null) {
                repository.save(entry.setDisplayOrder(order++));
            }
        }
        return ResponseEntity.noContent().build();
    }
}
