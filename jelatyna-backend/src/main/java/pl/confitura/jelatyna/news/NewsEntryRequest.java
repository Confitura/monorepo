package pl.confitura.jelatyna.news;

import java.time.Instant;

/** Payload for a new News Entry; without a slug, it takes one from the title. */
public record NewsEntryRequest(
        String title,
        String slug,
        String body,
        Instant date,
        Boolean published) {
}
