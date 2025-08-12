package pl.confitura.jelatyna.infrastructure.archive;

import pl.confitura.jelatyna.news.ListMonk;

import java.time.ZonedDateTime;
import java.util.List;

public record DumpedNews(
        News latest,
        List<News> all

) {

    public static DumpedNews from(List<ListMonk.NewsEntry> webpageNews) {
        if (webpageNews.isEmpty()) {
            return new DumpedNews(null, List.of());
        } else {
            var all = webpageNews.stream().map(News::from).toList();
            var latest = all.getLast();
            return new DumpedNews(latest, all);
        }
    }

    public record News(
            String title,
            String body,
            ZonedDateTime publishedAt) {
        public static News from(ListMonk.NewsEntry newsEntry) {
            return new News(newsEntry.title(), newsEntry.body(), newsEntry.publishedAt());
        }
    }
}
