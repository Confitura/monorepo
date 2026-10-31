package pl.confitura.jelatyna.identity.user.domain;

import java.util.Set;

/** Which Users speak, of any Edition: presentation owns the talks and their speakers, so a User doesn't map its talks. */
public interface Speakers {

    boolean isSpeaker(String userId);

    Set<String> speakerIds();

    /** Ids of the Users who speak at an accepted talk. */
    Set<String> acceptedSpeakerIds();
}
