package pl.confitura.jelatyna.presentation;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.AllArgsConstructor;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

@RepositoryRestController
@AllArgsConstructor
public class PresentationController {

    private PresentationRepository repository;
    private UserRepository userRepository;

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/accept")
    @Transactional
    public ResponseEntity<?> accept(@PathVariable String presentationId) {
        this.repository.findOne(presentationId).setStatus("accepted");
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/unaccept")
    @Transactional
    public ResponseEntity<?> unaccept(@PathVariable String presentationId) {
        this.repository.findOne(presentationId).setStatus("reported");
        return ResponseEntity.ok().build();

    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @GetMapping("/presentations/{presentationId}/cospeakers")
    public ResponseEntity<Resources<User>> getCospeakers(@PathVariable String presentationId) {
        Set<User> cospeakers = this.repository.findOne(presentationId).getCospeakers();
        return ResponseEntity.ok(new Resources<>(cospeakers));
    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @DeleteMapping("/presentations/{presentationId}/cospeakers/{email:.+}")
    public ResponseEntity<?> removeCospeaker(@PathVariable String presentationId, @PathVariable String email) {
        Presentation presentation = this.repository.findOne(presentationId);
        presentation.setCospeakers(removeCospeakerByEmail(email, presentation.getCospeakers()));
        repository.save(presentation);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    @PostMapping("/presentations/{presentationId}/cospeakers/{email:.+}")
    @Transactional
    public ResponseEntity<?> addCospeaker(@PathVariable String presentationId, @PathVariable String email) {
        User user = this.userRepository.findOneByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Presentation presentation = this.repository.findOne(presentationId);
        if (presentation.isOwnedBy(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You cannot add yourself as a speaker!");
        }
        if (presentation.hasCospeaker(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This speaker is already added to this presentation");
        }
        presentation.getCospeakers().add(user);
        return ResponseEntity.ok(user);
    }

    private Set<User> removeCospeakerByEmail(@PathVariable String email, Set<User> cospeakers) {
        return cospeakers.stream().filter(it -> !it.getEmail().equalsIgnoreCase(email)).collect(Collectors.toSet());
    }

}
