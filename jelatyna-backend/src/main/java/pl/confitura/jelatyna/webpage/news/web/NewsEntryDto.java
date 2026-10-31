package pl.confitura.jelatyna.webpage.news.web;

import pl.confitura.jelatyna.webpage.news.domain.NewsEntry;

import java.time.Instant;

/** Admin view of a News Entry. */
public record NewsEntryDto(
        String id,
        String title,
        String slug,
        String body,
        Instant date,
        boolean published,
        String cover) {

    public static NewsEntryDto from(NewsEntry e) {
        return new NewsEntryDto(e.getId(), e.getTitle(), e.getSlug(), e.getBody(), e.getDate(), e.isPublished(), e.getCover());
    }
}
