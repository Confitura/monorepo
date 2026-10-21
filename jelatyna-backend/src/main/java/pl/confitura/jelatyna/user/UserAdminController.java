package pl.confitura.jelatyna.user;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@RestController
@PreAuthorize("@security.isAdmin()")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class UserAdminController {

    private final UserRepository repository;
    private final LoginRepository loginRepository;

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
        Map<String, List<String>> providersByUserId = loginRepository.findAll().stream()
                .collect(groupingBy(login -> login.getUser().getId(), mapping(Login::getProvider, toList())));
        return repository.findAll().stream()
                .map(user -> new FullUser(user, providersByUserId.getOrDefault(user.getId(), List.of())))
                .collect(toList());
    }

    @PostMapping("/users/manual")
    @Transactional
    public ResponseEntity<FullUser> createManual(@RequestBody User user) {
        if (user.getOrigin() == null || user.getOrigin().isBlank()) {
            user.setOrigin("manual");
        }
        if (user.getPhoto() == null || user.getPhoto().isBlank()) {
            var gravatar = new com.timgroup.jgravatar.Gravatar(300, com.timgroup.jgravatar.GravatarRating.GENERAL_AUDIENCES, com.timgroup.jgravatar.GravatarDefaultImage.BLANK);
            String url = gravatar.getUrl(user.getEmail());
            user.setPhoto(url.replace("http:", "https:"));
        }
        var saved = repository.save(user);
        return ResponseEntity.ok(new FullUser(saved, List.of()));
    }

    public record FullUser(
            String id,
            List<String> loginProviders,
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

        public FullUser(User user, List<String> loginProviders) {
            this(
                    user.getId(),
                    loginProviders,
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
