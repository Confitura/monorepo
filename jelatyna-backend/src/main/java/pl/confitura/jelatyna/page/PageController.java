package pl.confitura.jelatyna.page;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PageController {
    private final PageRepository pageRepository;

    @GetMapping("/pages/{id}")
    public ResponseEntity<String> getPage(@PathVariable String id) {
        return pageRepository.findById(id)
                .map(Page::getContent)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pages")
    public List<String> getPages() {
        return pageRepository.findAll().stream()
                .map(Page::getId)
                .toList();
    }

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/pages/{id}")
    @Transactional
    public ResponseEntity<?> createPage(@PathVariable String id, @RequestBody PageContent content) {
        Page page = new Page();
        page.setId(id);
        page.setContent(content.content());
        pageRepository.save(page);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@security.isAdmin()")
    @PutMapping("/pages/{id}")
    @Transactional
    public ResponseEntity<?> updatePage(@PathVariable String id, @RequestBody PageContent content) {
        return pageRepository.findById(id)
                .map(page -> {
                    page.setContent(content.content());
                    pageRepository.save(page);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@security.isAdmin()")
    @DeleteMapping("/pages/{id}")
    @Transactional
    public void deletePage(@PathVariable String id) {
        pageRepository.deleteById(id);
    }

    public record PageContent(String content) {
    }
}
