package pl.confitura.jelatyna.conference.edition.domain;

import pl.confitura.jelatyna.edition.Edition;

/**
 * Published inside the transaction that makes {@code current} the Current Edition instead of {@code previous}:
 * an {@code @EventListener} shares that transaction, a {@code @TransactionalEventListener} runs once it commits.
 */
public record EditionMadeCurrent(Edition previous, Edition current) {
}
