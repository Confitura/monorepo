package pl.confitura.jelatyna.login.github;

import static com.github.scribejava.core.model.Verb.GET;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.oauth.OAuth20Service;
import pl.confitura.jelatyna.login.OAuthUserService;
import pl.confitura.jelatyna.user.User;

@Service
public class GitHubService {

    private OAuth20Service gitHub;
    private OAuthUserService oauthUserService;
    private ObjectMapper mapper;

    @Autowired
    public GitHubService(OAuthUserService oauthUserService, GitHubConfigurationProperties properties, ObjectMapper mapper) {
        this.oauthUserService = oauthUserService;
        this.gitHub = new ServiceBuilder(properties.getApiKey())
                .apiSecret(properties.getApiSecret())
                .callback(properties.getCallback())
                .build(GitHubApi.instance());
        this.mapper = mapper;
    }

    String getAuthorizationUrl() {
        return gitHub.getAuthorizationUrl();
    }

    User getUserFor(String code) {
        try {
            return doGetUser(code);
        } catch (Exception ex) {
            throw new RuntimeException("Error on fetching user from github", ex);
        }
    }

    private User doGetUser(String code)
            throws IOException, InterruptedException, ExecutionException {
        OAuth2AccessToken token = gitHub.getAccessToken(code);
        return oauthUserService.mapToUser(getGitHubUserFor(token));
    }

    private GitHubUser getGitHubUserFor(OAuth2AccessToken token)
            throws InterruptedException, ExecutionException, IOException {
        OAuthRequest request = new OAuthRequest(GET, "https://api.github.com/user");
        gitHub.signRequest(token, request);
        Response response = gitHub.execute(request);
        return mapper.readValue(response.getBody(), GitHubUser.class);
    }

}
