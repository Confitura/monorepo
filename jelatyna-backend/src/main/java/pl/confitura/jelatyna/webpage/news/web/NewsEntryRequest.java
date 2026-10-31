package pl.confitura.jelatyna.webpage.news.web;

import java.time.Instant;

/**
 * Payload for a new News Entry, or an update of one; without a slug, a new entry takes one from the
 * title. On update, null fields stay unchanged, and a new title leaves the slug as it is. The cover
 * is not set here but uploaded via POST /resources/news-entries/{id}; removeCover true removes it.
 */
public record NewsEntryRequest(
        String title,
        String slug,
        String body,
        Instant date,
        Boolean published,
        Boolean removeCover) {
}
