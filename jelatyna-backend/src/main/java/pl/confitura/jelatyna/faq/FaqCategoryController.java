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
 * Manages the FAQ category dictionary. All operations are admin-only (guarded on the
 * repository's save/deleteById). Categories carry their own visibility and ordering;
 * the public webpage never reads categories directly — it consumes the entry dump, whose
 * {@code category} field is the category name.
 */
@RestController
@RequestMapping("/faq-categories")
@RequiredArgsConstructor
public class FaqCategoryController {

    private final FaqCategoryRepository repository;
    private final FaqEntryRepository entryRepository;

    /** Admin: all categories in display order. */
    @GetMapping
    @PreAuthorize("@security.isAdmin()")
    public List<FaqCategoryDto> getFaqCategories() {
        return repository.findAllByOrderByDisplayOrderAsc().stream().map(FaqCategoryDto::from).toList();
    }

    /** Creates an (empty) category, appended to the end of the order. Rejects duplicate names. */
    @PostMapping
    @PreAuthorize("@security.isAdmin()")
    public ResponseEntity<FaqCategoryDto> createFaqCategory(@RequestBody FaqCategoryRequest request) {
        String name = request.name() == null ? "" : request.name().strip();
        if (name.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (repository.findByNameIgnoreCase(name).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        FaqCategory category = new FaqCategory()
                .setName(name)
                .setDisplayOrder((int) repository.count())
                .setPublished(request.published() == null || request.published());
        return ResponseEntity.status(HttpStatus.CREATED).body(FaqCategoryDto.from(repository.save(category)));
    }

    /** Renames a category and/or toggles its visibility. Rejects a rename that collides with another. */
    @PutMapping("/{id}")
    @PreAuthorize("@security.isAdmin()")
    public ResponseEntity<FaqCategoryDto> updateFaqCategory(@PathVariable String id,
                                                            @RequestBody FaqCategoryRequest request) {
        FaqCategory category = repository.findById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        if (request.name() != null) {
            String name = request.name().strip();
            if (name.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            var existing = repository.findByNameIgnoreCase(name);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            category.setName(name);
        }
        if (request.published() != null) {
            category.setPublished(request.published());
        }
        return ResponseEntity.ok(FaqCategoryDto.from(repository.save(category)));
    }

    /** Persists a new category order: each id's displayOrder becomes its index in the list. */
    @PutMapping("/order")
    @PreAuthorize("@security.isAdmin()")
    public ResponseEntity<Void> reorderFaqCategories(@RequestBody ReorderRequest request) {
        int order = 0;
        for (String id : request.ids()) {
            FaqCategory category = repository.findById(id);
            if (category != null) {
                repository.save(category.setDisplayOrder(order++));
            }
        }
        return ResponseEntity.noContent().build();
    }

    /** Moves every entry from {@code from} into {@code to}, then deletes the emptied {@code from}. */
    @PostMapping("/merge")
    @PreAuthorize("@security.isAdmin()")
    public ResponseEntity<Void> mergeFaqCategories(@RequestBody MergeRequest request) {
        if (request.from() == null || request.to() == null || request.from().equals(request.to())) {
            return ResponseEntity.badRequest().build();
        }
        FaqCategory from = repository.findById(request.from());
        FaqCategory to = repository.findById(request.to());
        if (from == null || to == null) {
            return ResponseEntity.notFound().build();
        }
        entryRepository.moveEntries(from, to);
        repository.deleteById(from.getId());
        return ResponseEntity.noContent().build();
    }

    /** Deletes an empty category; refuses (409) while it still has entries. */
    @DeleteMapping("/{id}")
    @PreAuthorize("@security.isAdmin()")
    public ResponseEntity<Void> deleteFaqCategory(@PathVariable String id) {
        FaqCategory category = repository.findById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        if (entryRepository.countByCategoryId(id) > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record MergeRequest(String from, String to) {
    }
}
