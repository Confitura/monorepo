package pl.confitura.jelatyna.conference.registration.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pl.confitura.jelatyna.identity.user.domain.Speakers;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationData;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationRepository;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

/** Gives a User their ticket, under their /users/{userId} path. */
@RestController
@RequiredArgsConstructor
public class ParticipationController {

    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final Speakers speakers;

    @PostMapping("/users/{userId}/participationData")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<?> assignParticipationData(
            @PathVariable String userId,
            @RequestBody ParticipationData participationData) {
        User current = userRepository.findById(userId);
        ParticipationData ticket = participationRepository.findById(participationData.getId());
        // An unknown ticket id clears the User's ticket, as it did when User mapped the ticket itself.
        current.setParticipationDataId(ticket == null ? null : ticket.getId());
        return ResponseEntity.ok(new UserWithTicket(userRepository.save(current), ticket, speakers.isSpeaker(userId)));
    }
}
