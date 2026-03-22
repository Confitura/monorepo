package pl.confitura.jelatyna.infrastructure.published;

import pl.confitura.jelatyna.news.ListmonkApi;

import java.time.ZonedDateTime;
import java.util.List;

public record DumpedNews(
        News latest,
        List<News> all

) {

    public static DumpedNews from(List<ListmonkApi.NewsEntry> webpageNews) {
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
            String body,
            ZonedDateTime publishedAt) {
        public static News from(ListmonkApi.NewsEntry newsEntry) {
            return new News(newsEntry.title(), newsEntry.body(), newsEntry.publishedAt());
        }
    }
}
