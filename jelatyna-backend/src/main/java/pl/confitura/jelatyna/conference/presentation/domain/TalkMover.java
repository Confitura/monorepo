package pl.confitura.jelatyna.conference.presentation.domain;

import java.util.List;

import org.springframework.stereotype.Component;

import pl.confitura.jelatyna.identity.user.domain.AbsorbedUserTalks;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.user.User;

@Component
public class TalkMover implements AbsorbedUserTalks {

    @Override
    public List<String> talksToTake(User survivor, User absorbed) {
        return gained(survivor, absorbed).stream().map(Presentation::getTitle).toList();
    }

    // The survivor stays a single speaker of a talk both gave. Speakers are matched by id: absorbing may have changed
    // the survivor's equality fields.
    @Override
    public int takeTalks(User survivor, User absorbed) {
        List<Presentation> gained = gained(survivor, absorbed);
        absorbed.getPresentations().forEach(talk -> talk.getSpeakers().removeIf(speaker -> speaker.getId().equals(absorbed.getId())));
        gained.forEach(talk -> talk.getSpeakers().add(survivor));
        // Also the inverse side, read by isSpeaker() for the survivor's fresh token.
        survivor.getPresentations().addAll(gained);
        return gained.size();
    }

    // Not those the survivor already speaks at alongside the absorbed User.
    private static List<Presentation> gained(User survivor, User absorbed) {
        return absorbed.getPresentations().stream().filter(talk -> !speaksAt(survivor, talk)).toList();
    }

    private static boolean speaksAt(User user, Presentation talk) {
        return talk.getSpeakers().stream().anyMatch(speaker -> speaker.getId().equals(user.getId()));
    }
}
