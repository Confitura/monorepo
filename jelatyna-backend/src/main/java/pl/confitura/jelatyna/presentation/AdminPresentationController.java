package pl.confitura.jelatyna.presentation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "bearerAuth")
public class AdminPresentationController {

    private final PresentationRepository repository;
    private final TagRepository tagRepository;

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/accept")
    @Transactional
    public ResponseEntity<?> accept(@PathVariable String presentationId) {
        this.repository.findById(presentationId).setAccepted(true);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/reject")
    @Transactional
    public ResponseEntity<?> reject(@PathVariable String presentationId) {
        this.repository.findById(presentationId).setAccepted(false);
        return ResponseEntity.ok().build();

    }


    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/tags")
    public ResponseEntity<?> saveTags(@RequestBody Iterable<Tag> tags) {
        return ResponseEntity.ok(tagRepository.saveAll(tags));
    }
}
