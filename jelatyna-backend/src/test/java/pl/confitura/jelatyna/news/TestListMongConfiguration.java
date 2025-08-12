package pl.confitura.jelatyna.news;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@TestConfiguration
public class TestListMongConfiguration {

    @Bean
    NewsletterApi listMonk() {
        return new FakeNewsletterApi();
    }

    class FakeNewsletterApi implements NewsletterApi {

        @Override
        public List<NewsEntry> getWebpageNews() throws IOException {
            return List.of(
                    new NewsEntry("Test Title 2", "Test Content 2", ZonedDateTime.parse("2025-08-12T12:00Z")),
                    new NewsEntry("Test Title 1", "Test Content 1", ZonedDateTime.parse("2025-08-13T12:00Z")),
                    new NewsEntry("Test Title 3", "Test Content 3", ZonedDateTime.parse("2025-08-11T12:00Z"))
            );
        }

        @Override
        public int getSubscribersCount() throws IOException {
            return 42;
        }
    }
}
