package pl.confitura.jelatyna.news;

import com.eventuallycoding.listmonk.ListmonkClient;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class ListmonkApi implements NewsletterApi {

    private final ListmonkClient listmonkClient;

    @Override
    public int getSubscribersCount() throws IOException {
        var response = listmonkClient.getSubscribersApi().getSubscribers(
                0, 0, null, null, null, null, null
        ).execute();
        return response.body().getData().getTotal();
    }

}
