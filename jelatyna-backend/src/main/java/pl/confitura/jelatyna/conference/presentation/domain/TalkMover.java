package pl.confitura.jelatyna.conference.presentation.domain;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pl.confitura.jelatyna.identity.user.domain.AbsorbedUserTalks;
import pl.confitura.jelatyna.user.User;

@Component
@RequiredArgsConstructor
public class TalkMover implements AbsorbedUserTalks {

    private final PresentationRepository presentations;

    @Override
    public List<String> talksToTake(User survivor, User absorbed) {
        return gained(survivor, presentations.findBySpeakersContains(absorbed)).stream().map(Presentation::getTitle).toList();
    }

    // The survivor stays a single speaker of a talk both gave. Speakers are matched by id: absorbing may have changed
    // the survivor's equality fields.
    @Override
    public int takeTalks(User survivor, User absorbed) {
        List<Presentation> talks = presentations.findBySpeakersContains(absorbed);
        List<Presentation> gained = gained(survivor, talks);
        talks.forEach(talk -> talk.getSpeakers().removeIf(speaker -> speaker.getId().equals(absorbed.getId())));
        gained.forEach(talk -> talk.getSpeakers().add(survivor));
        return gained.size();
    }

    // Not those the survivor already speaks at alongside the absorbed User.
    private static List<Presentation> gained(User survivor, List<Presentation> talks) {
        return talks.stream().filter(talk -> !speaksAt(survivor, talk)).toList();
    }

    private static boolean speaksAt(User user, Presentation talk) {
        return talk.getSpeakers().stream().anyMatch(speaker -> speaker.getId().equals(user.getId()));
    }
}
