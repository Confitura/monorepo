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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.identity.user.domain.AbsorbedUserTalks;
import pl.confitura.jelatyna.identity.user.domain.Speakers;
import pl.confitura.jelatyna.platform.security.JelatynaPrincipal;
import pl.confitura.jelatyna.infrastructure.security.TokenService;

/**
 * A User's Logins, and linking another one (ADR-0003): the person completes that provider's sign-in
 * while signed in, which yields a second token, and the User it names is merged into the signed-in one.
 * An admin merges any two Users by id instead. A User unlinks any Login but their last; an admin unlinks any.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class UserLoginsController {

    // The second token proves the person has just signed in as the other User, so its auth_time must be this recent.
    static final Duration PROOF_MAX_AGE = Duration.ofMinutes(5);

    private final UserRepository userRepository;
    private final LoginRepository loginRepository;
    private final TokenService tokenService;
    private final AbsorbedUserTalks absorbedUserTalks;
    private final Speakers speakers;

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
        Moved moved = merge(survivor, absorbed);
        // Ids only: names and emails stay out of the logs.
        moved.logins().forEach(login -> log.atInfo().setMessage("Linked login")
                .addKeyValue("actorUserId", principal.getId())
                .addKeyValue("survivorUserId", survivor.getId())
                .addKeyValue("absorbedUserId", absorbed.getId())
                .addKeyValue("provider", login.getProvider())
                .addKeyValue("loginId", login.getId())
                .addKeyValue("talksMoved", moved.talks())
                .log());
        // Fresh because its roles may have changed; the survivor didn't just sign in, so it keeps its sign-in time.
        return ResponseEntity.ok(tokenService.asToken(survivor, principal.getAuthTime()));
    }

    /** An admin merges any two Users, keeping the one they choose: the only way to join a Manual User, which has no Login. */
    @PostMapping("/users/{survivorId}/merge/{absorbedId}")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Void> adminMergeUsers(@AuthenticationPrincipal JelatynaPrincipal principal,
                                                @PathVariable String survivorId, @PathVariable String absorbedId) {
        User survivor = survivorForAdmin(survivorId, absorbedId);
        Moved moved = merge(survivor, userToAbsorb(survivor, absorbedId));
        log.atInfo().setMessage("Merged users")
                .addKeyValue("actorUserId", principal.getId())
                .addKeyValue("byAdmin", true)
                .addKeyValue("survivorUserId", survivorId)
                .addKeyValue("absorbedUserId", absorbedId)
                .addKeyValue("loginsMoved", moved.logins().size())
                .addKeyValue("talksMoved", moved.talks())
                .log();
        return ResponseEntity.noContent().build();
    }

    /** What {@link #mergeUsers} would make of the signed-in User, shown before the person confirms; changes nothing. */
    @PostMapping("/users/merge/preview")
    @Transactional(readOnly = true)
    public MergePreview previewMerge(@AuthenticationPrincipal JelatynaPrincipal principal,
                                     @RequestBody MergeUsersRequest request) {
        User absorbed = userToAbsorb(principal, request);
        User survivor = userRepository.findById(principal.getId());
        return MergePreview.of(survivor, absorbed, absorbedUserTalks.talksToTake(survivor, absorbed));
    }

    /** What {@link #adminMergeUsers} would make of the survivor, shown before the admin confirms; changes nothing. */
    @GetMapping("/users/{survivorId}/merge/{absorbedId}/preview")
    @PreAuthorize("@security.isAdmin()")
    @Transactional(readOnly = true)
    public MergePreview previewAdminMerge(@PathVariable String survivorId, @PathVariable String absorbedId) {
        User survivor = survivorForAdmin(survivorId, absorbedId);
        User absorbed = userToAbsorb(survivor, absorbedId);
        return MergePreview.of(survivor, absorbed, absorbedUserTalks.talksToTake(survivor, absorbed));
    }

    /**
     * The person declined the merge: deletes the User the second sign-in created, with its Login, so nothing is
     * left behind. Refuses a User with content, which {@link #mergeUsers} may absorb but cancelling must not delete.
     */
    @PostMapping("/users/merge/cancel")
    @Transactional
    public ResponseEntity<Void> cancelMerge(@AuthenticationPrincipal JelatynaPrincipal principal,
                                            @RequestBody MergeUsersRequest request) {
        User absorbed = userToAbsorb(principal, request);
        if (absorbed.hasContent() || speakers.isSpeaker(absorbed.getId())) {
            throw new ResponseStatusException(CONFLICT, "The other user has a profile or roles");
        }
        loginRepository.findByUserId(absorbed.getId()).forEach(loginRepository::delete);
        userRepository.delete(absorbed);
        return ResponseEntity.noContent().build();
    }

    private User userToAbsorb(JelatynaPrincipal principal, MergeUsersRequest request) {
        if (principal == null) {
            throw new ResponseStatusException(UNAUTHORIZED);
        }
        String absorbedId = tokenService.userIdIfSignedInWithin(request.token(), PROOF_MAX_AGE)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "The second sign-in is not recent enough"));
        if (absorbedId.equals(principal.getId())) {
            throw new ResponseStatusException(CONFLICT, "The login already belongs to the signed-in user");
        }
        return userToAbsorb(userRepository.findById(principal.getId()), absorbedId);
    }

    // An admin names both Users by id, so the survivor may be missing too.
    private User survivorForAdmin(String survivorId, String absorbedId) {
        if (survivorId.equals(absorbedId)) {
            throw new ResponseStatusException(CONFLICT, "A user cannot be merged with itself");
        }
        User survivor = userRepository.findById(survivorId);
        if (survivor == null) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return survivor;
    }

    // The refusals every merge shares, self-service or by an admin, once the two Users differ.
    private User userToAbsorb(User survivor, String absorbedId) {
        User absorbed = userRepository.findById(absorbedId);
        // Already merged (e.g. Confirm sent twice) or cancelled in another tab.
        if (absorbed == null) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        // A User holds at most one ticket, so two block the merge; the reason sends the person to an admin.
        if (absorbed.isParticipant() && survivor.isParticipant()) {
            throw new ResponseStatusException(CONFLICT, "Both users have a ticket, so they cannot be merged. Please contact an admin.");
        }
        return absorbed;
    }

    // Merge (users): the survivor absorbs the other User's profile gaps, roles, talks, ticket and Logins, and the other
    // User is deleted. Answers what moved, for the log.
    private Moved merge(User survivor, User absorbed) {
        survivor.absorb(absorbed);
        int talksMoved = absorbedUserTalks.takeTalks(survivor, absorbed);
        survivor.takeTicket(absorbed);
        List<Login> logins = loginRepository.findByUserId(absorbed.getId());
        logins.forEach(login -> login.moveTo(survivor));
        userRepository.delete(absorbed);
        return new Moved(logins, talksMoved);
    }

    private record Moved(List<Login> logins, int talks) {
    }

    // Boot's error page drops the reason; the admin app shows it, e.g. what blocks a merge.
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<String> refusal(ResponseStatusException refusal) {
        return ResponseEntity.status(refusal.getStatusCode()).body(refusal.getReason());
    }

    public record MergeUsersRequest(String token) {
    }

    /** The survivor's profile after a merge, the roles it would gain, and the titles of the talks it would gain. */
    public record MergePreview(String name, String email, String bio, String username, String twitter, String github,
                               String www, String photo, boolean becomesAdmin, boolean becomesVolunteer,
                               List<String> talksToMove) {

        static MergePreview of(User survivor, User absorbed, List<String> talksToMove) {
            User merged = survivor.mergedWith(absorbed);
            return new MergePreview(merged.getName(), merged.getEmail(), merged.getBio(), merged.getUsername(),
                    merged.getTwitter(), merged.getGithub(), merged.getWww(), merged.getPhoto(),
                    merged.isAdmin() && !survivor.isAdmin(), merged.isVolunteer() && !survivor.isVolunteer(),
                    talksToMove);
        }
    }
}
