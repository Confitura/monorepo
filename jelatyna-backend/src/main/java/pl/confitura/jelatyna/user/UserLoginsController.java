package pl.confitura.jelatyna.user;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.time.Duration;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.infrastructure.security.TokenService;

/**
 * A User's Logins, and linking another one (ADR-0003): the person completes that provider's sign-in
 * while signed in, which yields a second token, and the User it names is merged into the signed-in one.
 * A User unlinks any Login but their last; an admin unlinks any.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class UserLoginsController {

    // The second token proves the person has just signed in as the other User, so it must be this recent.
    static final Duration PROOF_MAX_AGE = Duration.ofMinutes(5);

    private final UserRepository userRepository;
    private final LoginRepository loginRepository;
    private final TokenService tokenService;

    @GetMapping("/users/{id}/logins")
    @PreAuthorize("@security.isOwner(#id)")
    public List<LoginDto> getUserLogins(@PathVariable String id) {
        return loginRepository.findByUserId(id).stream().map(LoginDto::from).toList();
    }

    /** Unlinks a Login: deletes it, so signing in with it again acts like a never-seen Login. */
    @DeleteMapping("/users/{id}/logins/{loginId}")
    @PreAuthorize("@security.isOwner(#id)")
    @Transactional
    public ResponseEntity<Void> unlinkLogin(@AuthenticationPrincipal JelatynaPrincipal principal,
                                            @PathVariable String id, @PathVariable String loginId) {
        List<Login> logins = loginRepository.findByUserId(id);
        // Looked up among this User's Logins, so a path naming someone else's Login finds nothing.
        Login login = logins.stream()
                .filter(candidate -> candidate.getId().equals(loginId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        // An admin may leave a User with no Login, like a Manual User.
        if (logins.size() == 1 && !principal.isAdmin()) {
            throw new ResponseStatusException(CONFLICT, "The last login cannot be removed: the user could not sign in again");
        }
        loginRepository.delete(login);
        log.atInfo().setMessage("Unlinked login")
                .addKeyValue("actorUserId", principal.getId())
                .addKeyValue("userId", id)
                .addKeyValue("provider", login.getProvider())
                .addKeyValue("loginId", login.getId())
                .log();
        return ResponseEntity.noContent().build();
    }

    /** Merges the User the second token names into the signed-in one; answers with a fresh token for the latter. */
    @PostMapping("/users/merge")
    @Transactional
    public ResponseEntity<String> mergeUsers(@AuthenticationPrincipal JelatynaPrincipal principal,
                                             @RequestBody MergeUsersRequest request) {
        User absorbed = userToAbsorb(principal, request);
        User survivor = userRepository.findById(principal.getId());
        loginRepository.findByUserId(absorbed.getId()).forEach(login -> {
            login.moveTo(survivor);
            // Ids only: names and emails stay out of the logs.
            log.atInfo().setMessage("Linked login")
                    .addKeyValue("actorUserId", principal.getId())
                    .addKeyValue("survivorUserId", survivor.getId())
                    .addKeyValue("absorbedUserId", absorbed.getId())
                    .addKeyValue("provider", login.getProvider())
                    .addKeyValue("loginId", login.getId())
                    .log();
        });
        userRepository.delete(absorbed);
        return ResponseEntity.ok(tokenService.asToken(survivor));
    }

    /**
     * The person declined the merge: deletes the User the second sign-in created, with its Login, so nothing is
     * left behind. Refuses exactly when {@link #mergeUsers} would, so it never deletes a User with content.
     */
    @PostMapping("/users/merge/cancel")
    @Transactional
    public ResponseEntity<Void> cancelMerge(@AuthenticationPrincipal JelatynaPrincipal principal,
                                            @RequestBody MergeUsersRequest request) {
        User absorbed = userToAbsorb(principal, request);
        loginRepository.findByUserId(absorbed.getId()).forEach(loginRepository::delete);
        userRepository.delete(absorbed);
        return ResponseEntity.noContent().build();
    }

    private User userToAbsorb(JelatynaPrincipal principal, MergeUsersRequest request) {
        if (principal == null) {
            throw new ResponseStatusException(UNAUTHORIZED);
        }
        String absorbedId = tokenService.userIdIfIssuedWithin(request.token(), PROOF_MAX_AGE)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "The second sign-in is not recent enough"));
        if (absorbedId.equals(principal.getId())) {
            throw new ResponseStatusException(CONFLICT, "The login already belongs to the signed-in user");
        }
        User absorbed = userRepository.findById(absorbedId);
        // Already merged (e.g. Confirm sent twice) or cancelled in another tab.
        if (absorbed == null) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        // Merging two Users that both hold something comes later.
        if (absorbed.hasContent()) {
            throw new ResponseStatusException(CONFLICT, "The other user has a profile, talks, roles or a ticket");
        }
        return absorbed;
    }

    public record MergeUsersRequest(String token) {
    }
}
