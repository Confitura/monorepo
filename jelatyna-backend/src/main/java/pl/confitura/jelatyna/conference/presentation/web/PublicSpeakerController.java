package pl.confitura.jelatyna.conference.presentation.web;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import pl.confitura.jelatyna.conference.presentation.domain.PublicSpeaker;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

/** Speakers' public profiles with their talks, under the /users paths they have always had. */
@RestController
@RequiredArgsConstructor
public class PublicSpeakerController {

    private final UserRepository userRepository;
    private final PresentationRepository presentationRepository;

    @GetMapping("/users/{id}/public")
    public ResponseEntity<?> getPublicById(@PathVariable String id) {
        User user = userRepository.findById(id);
        return ResponseEntity.ok(PublicSpeaker.from(user, presentationRepository.findBySpeakersContains(user)));
    }

    // Accepted talks of every Edition.
    @GetMapping("/users/search/speakers")
    public ResponseEntity<Set<PublicSpeaker>> getSpeakers() {
        return ResponseEntity.ok(PublicSpeaker.of(presentationRepository.findAccepted()));
    }
}
