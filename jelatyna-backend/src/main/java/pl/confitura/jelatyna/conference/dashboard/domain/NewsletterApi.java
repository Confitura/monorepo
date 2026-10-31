package pl.confitura.jelatyna.conference.dashboard.domain;

import java.io.IOException;

public interface NewsletterApi {
    int getSubscribersCount() throws IOException;
}
