package pl.confitura.jelatyna.news;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

public interface NewsletterApi {
    List<NewsEntry> getWebpageNews() throws IOException;

    int getSubscribersCount() throws IOException;

    record NewsEntry(
            String title,
            String body,
            ZonedDateTime publishedAt
    ) {
    }
}
