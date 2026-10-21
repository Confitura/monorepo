package pl.confitura.jelatyna.infrastructure.published;

import pl.confitura.jelatyna.news.NewsEntry;

import java.time.Instant;
import java.util.List;

public record DumpedNews(
        News latest,
        List<News> all

) {

    public static DumpedNews from(List<NewsEntry> webpageNews) {
        if (webpageNews.isEmpty()) {
            return new DumpedNews(null, List.of());
        } else {
            var all = webpageNews.stream().map(News::from).toList();
            var latest = all.getFirst();
            return new DumpedNews(latest, all);
        }
    }

    public record News(
            String title,
            String slug,
            String body,
            Instant publishedAt) {
        public static News from(NewsEntry newsEntry) {
            return new News(newsEntry.getTitle(), newsEntry.getSlug(), newsEntry.getBody(), newsEntry.getDate());
        }
    }
}
