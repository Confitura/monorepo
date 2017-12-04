package pl.confitura.jelatyna.login.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import pl.confitura.jelatyna.login.AbstractOAuth20Service;
import pl.confitura.jelatyna.login.OAuthConfiguration;
import pl.confitura.jelatyna.login.OAuthUserService;

import java.io.IOException;

public class GithubService extends AbstractOAuth20Service {
    public static final String SYSTEM = "github";

    public GithubService(OAuthUserService oauthUserService, OAuthConfiguration.OAuthProviderProperties properties, ObjectMapper mapper) {
        super(oauthUserService, properties, mapper);
    }

    @Override
    protected OAuth20Service createService(OAuthConfiguration.OAuthProviderProperties properties) {
        return new ServiceBuilder(properties.getApiKey())
                .apiSecret(properties.getApiSecret())
                .callback(properties.getCallback())
                .build(GitHubApi.instance());
    }

    @Override
    protected String getProtectedUserUrl() {
        return "https://api.github.com/user";
    }

    @Override
    protected GitHubUser mapToUser(String body) throws IOException {
        return mapper.readValue(body, GitHubUser.class);
    }
}
