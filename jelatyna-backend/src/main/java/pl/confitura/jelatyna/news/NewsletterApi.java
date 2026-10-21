package pl.confitura.jelatyna.news;

import java.io.IOException;

public interface NewsletterApi {
    int getSubscribersCount() throws IOException;
}
