package pl.confitura.jelatyna.login.facebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.login.OAuthUser;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Service
class FacebookService {

    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v2.8/me";

    private final OAuth20Service service;
    private final ObjectMapper mapper;

    public FacebookService(FacebookConfigurationProperties config, ObjectMapper mapper) {
        final String secretState = "secret" + new Random().nextInt(999_999);
        service = new ServiceBuilder()
                .apiKey(config.getApiKey())
                .apiSecret(config.getApiSecret())
                .state(secretState)
                .callback(config.getCallback())
                .build(FacebookApi.instance());
        this.mapper = mapper;
    }

    private OAuthUser mapToUser(String body) throws IOException {
        return mapper.readValue(body, OAuthUser.class);
    }

    String getAuthorizationUrl() {
        return service.getAuthorizationUrl();
    }

    OAuth2AccessToken getAccessToken(String code) {
        try {
            return service.getAccessToken(code);
        } catch (Exception e) {
            throw new RuntimeException("Exception while loading request token", e);
        }
    }

    OAuthUser getUser(OAuth2AccessToken accessToken) {
        try {
            return doGetUser(accessToken);
        } catch (Exception e) {
            throw new RuntimeException("Exception while validating a user", e);
        }
    }

    private OAuthUser doGetUser(OAuth2AccessToken accessToken) throws InterruptedException, ExecutionException, IOException {
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        final Response response = service.execute(request);
        return mapToUser(response.getBody());
    }
}

