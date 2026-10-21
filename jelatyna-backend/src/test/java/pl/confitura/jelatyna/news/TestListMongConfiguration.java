package pl.confitura.jelatyna.news;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@TestConfiguration
public class TestListMongConfiguration {

    @Bean
    NewsletterApi listMonk() {
        return new FakeNewsletterApi();
    }

    class FakeNewsletterApi implements NewsletterApi {

        @Override
        public int getSubscribersCount() throws IOException {
            return 42;
        }
    }
}
