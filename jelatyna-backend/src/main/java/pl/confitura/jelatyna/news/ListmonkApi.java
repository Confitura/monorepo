package pl.confitura.jelatyna.news;

import com.eventuallycoding.listmonk.ListmonkClient;
import com.eventuallycoding.listmonk.models.Campaign;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ListmonkApi implements NewsletterApi {

    private final ListmonkClient listmonkClient;

    @Override
    public List<NewsEntry> getWebpageNews() throws IOException {
        var response = listmonkClient.getCampaignsApi().getCampaigns(0, 100).execute();
        List<Campaign> campaigns = response.body().getData().getResults();
        return campaigns
                .stream().filter(it -> Arrays.asList(it.getTags()).contains("webpage"))
                .map(x -> new NewsEntry(x.getName(), x.getBody(), x.getCreatedAt()))
                .toList();
    }

    @Override
    public int getSubscribersCount() throws IOException {
        var response = listmonkClient.getSubscribersApi().getSubscribers(
                0, 0, null, null, null, null, null
        ).execute();
        return response.body().getData().getTotal();
    }

}
