package pl.confitura.jelatyna.conference.voting.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.conference.edition.domain.EditionMadeCurrent;

/** Clears the voter IPs of an Edition that stops being current, in the transaction that switches it (ADR-0004). */
@Component
@RequiredArgsConstructor
@Slf4j
public class VoterIpsCleaner {

    private final VoteRepository voteRepository;

    @EventListener
    public void clearVoterIpsOfThePreviousEdition(EditionMadeCurrent event) {
        int cleared = voteRepository.clearVoterIpsIn(event.previous());
        // Ids only: no IP addresses in the logs.
        log.atInfo().setMessage("Cleared the voter IPs of an Edition that stopped being current")
                .addKeyValue("editionId", event.previous().getId())
                .addKeyValue("votesCleared", cleared)
                .log();
    }
}
