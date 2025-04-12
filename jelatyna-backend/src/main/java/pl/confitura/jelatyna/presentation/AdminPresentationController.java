package pl.confitura.jelatyna.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.presentation.rating.RatingService;
import pl.confitura.jelatyna.user.UserRepository;

@RequiredArgsConstructor
@RestController
public class AdminPresentationController {

    private final PresentationRepository repository;
    private final UserRepository userRepository;
    private final RatingService ratingService;
    private final TagRepository tagRepository;

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/accept")
    @Transactional
    public ResponseEntity<?> accept(@PathVariable String presentationId) {
        this.repository.findById(presentationId).setAccepted(true);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/unaccept")
    @Transactional
    public ResponseEntity<?> unaccept(@PathVariable String presentationId) {
        this.repository.findById(presentationId).setAccepted(false);
        return ResponseEntity.ok().build();

    }


    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/tags")
    public ResponseEntity<?> saveTags(@RequestBody Iterable<Tag> tags) {
        return ResponseEntity.ok(tagRepository.saveAll(tags));
    }
}
