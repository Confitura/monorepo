package pl.confitura.jelatyna.login.github;

import static com.github.scribejava.core.model.Verb.GET;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
    @Value("${github.api-key}")
    private String apiKey;
    @Value("${github.api-secret}")
    private String apiSecret;
    @Value("${github.callback}")
    private String callback;
    private OAuthUserService oauthUserService;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public GitHubService(OAuthUserService oauthUserService) {
        this.oauthUserService = oauthUserService;
    }

    @PostConstruct
    public void createService() {
        gitHub = new ServiceBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(callback)
                .build(GitHubApi.instance());
    }

    String getAuthorizationUrl() {
        return gitHub.getAuthorizationUrl();
    }

    User getUserFor(@RequestParam("code") String code, GithubLoginController githubLoginController) {
        try {
            return doGetUser(code);
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }

    private User doGetUser(@RequestParam("code") String code)
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
