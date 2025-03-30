package pl.confitura.jelatyna.page;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
