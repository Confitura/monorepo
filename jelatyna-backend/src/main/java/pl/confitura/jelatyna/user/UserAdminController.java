package pl.confitura.jelatyna.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@PreAuthorize("@security.isAdmin()")
@RequiredArgsConstructor
@Slf4j
public class UserAdminController {

    private final UserRepository repository;

    @PostMapping("/users/{userId}/volunteer/{isVolunteer}")
    @Transactional
    public ResponseEntity<?> markAsVolunteer(@PathVariable("userId") String userId,
                                                  @PathVariable("isVolunteer") boolean isVolunteer) {
        User user = repository.findById(userId);
        user.setVolunteer(isVolunteer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/admin/{isAdmin}")
    @Transactional
    public ResponseEntity<?> markAsAdmin(@PathVariable("userId") String userId,
                                              @PathVariable("isAdmin") boolean isAdmin) {
        User user = repository.findById(userId);
        user.setAdmin(isAdmin);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public List<FullUser> getAllUsers() {
        return repository.findAll().stream().map(FullUser::new).collect(toList());
    }

    public record FullUser(
            String id,
            String origin,
            String name,
            String email,
            String bio,
            String username,
            String twitter,
            String github,
            String www,
            String photo,
            boolean isAdmin,
            boolean isVolunteer,
            boolean isSpeaker,
            boolean privacyPolicyAccepted,
            boolean isParticipant,
            boolean hasAcceptedPresentation) {

        public FullUser(User user) {
            this(
                    user.getId(),
                    user.getOrigin(),
                    user.getName(),
                    user.getEmail(),
                    user.getBio(),
                    user.getUsername(),
                    user.getTwitter(),
                    user.getGithub(),
                    user.getWww(),
                    user.getPhoto(),
                    user.isAdmin(),
                    user.isVolunteer(),
                    user.isSpeaker(),
                    user.getPrivacyPolicyAccepted(),
                    user.isParticipant(),
                    user.hasAcceptedPresentation()
            );
        }
    }


}
