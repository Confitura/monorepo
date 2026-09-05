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

import java.util.List;

/**
 * Manages FAQ questions individually. Public reads expose only published entries;
 * writes are admin-only (guarded on the repository's save/deleteById).
 */
@RestController
@RequestMapping("/faq-entries")
@RequiredArgsConstructor
public class FaqEntryController {

    private final FaqEntryRepository repository;

    /** Public: published entries, grouped-ready (ordered by category then displayOrder). */
    @GetMapping
    public List<FaqEntryDto> published() {
        return repository.findPublishedOrdered().stream().map(FaqEntryDto::from).toList();
    }

    /** Admin: all entries including unpublished, for the management screen. */
    @GetMapping("/all")
    @PreAuthorize("@security.isAdmin()")
    public List<FaqEntryDto> all() {
        return repository.findAllOrdered().stream().map(FaqEntryDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaqEntryDto> get(@PathVariable String id) {
        FaqEntry entry = repository.findById(id);
        return entry == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(FaqEntryDto.from(entry));
    }

    @PostMapping
    public ResponseEntity<FaqEntryDto> create(@RequestBody FaqEntryRequest request) {
        FaqEntry entry = new FaqEntry()
                .setCategory(request.category())
                .setQuestion(request.question())
                .setAnswer(request.answer())
                .setDisplayOrder(request.displayOrder() == null ? 0 : request.displayOrder())
                .setPublished(Boolean.TRUE.equals(request.published()));
        return ResponseEntity.status(HttpStatus.CREATED).body(FaqEntryDto.from(repository.save(entry)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FaqEntryDto> update(@PathVariable String id, @RequestBody FaqEntryRequest request) {
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
    public ResponseEntity<Void> delete(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** Persists a new order: each id's displayOrder becomes its index in the list. */
    @PutMapping("/order")
    public ResponseEntity<Void> reorder(@RequestBody ReorderRequest request) {
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
