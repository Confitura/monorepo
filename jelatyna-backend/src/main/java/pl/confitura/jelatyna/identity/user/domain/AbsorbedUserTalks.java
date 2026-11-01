package pl.confitura.jelatyna.identity.user.domain;

import java.util.List;

/**
 * Merge (users): the absorbed User's talks, which presentation owns, move to the survivor. Called inside the
 * merge's transaction, before the absorbed User is deleted.
 */
public interface AbsorbedUserTalks {

    /** Titles of the absorbed User's talks the survivor would gain, for the merge preview; changes nothing. */
    List<String> talksToTake(User survivor, User absorbed);

    /** Moves the absorbed User's talks to the survivor and answers how many it gained. */
    int takeTalks(User survivor, User absorbed);
}
