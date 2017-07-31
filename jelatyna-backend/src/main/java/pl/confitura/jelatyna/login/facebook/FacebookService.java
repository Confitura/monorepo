package pl.confitura.jelatyna.login.facebook;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import pl.confitura.jelatyna.login.OAuthUserService;
import pl.confitura.jelatyna.user.User;

@Service
class FacebookService {

    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v2.8/me";

    private final OAuth20Service service;
    private final ObjectMapper mapper;
    private OAuthUserService oauthUserService;

    @Autowired
    public FacebookService(FacebookConfigurationProperties config, ObjectMapper mapper, OAuthUserService oAuthUserService) {
        final String secretState = "secret" + new Random().nextInt(999_999);
        this.oauthUserService = oAuthUserService;
        this.mapper = mapper;
        this.service = new ServiceBuilder(config.getApiKey())
                .apiSecret(config.getApiSecret())
                .state(secretState)
                .callback(config.getCallback())
                .build(FacebookApi.instance());
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

    User getUser(String code) {
        try {
            final OAuth2AccessToken accessToken = getAccessToken(code);
            return oauthUserService.mapToUser(doGetUser(accessToken));
        } catch (Exception e) {
            throw new RuntimeException("Exception while validating a user", e);
        }
    }

    private FacebookUser doGetUser(OAuth2AccessToken accessToken) throws InterruptedException, ExecutionException, IOException {
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        final Response response = service.execute(request);
        return mapToUser(response.getBody());
    }

    private FacebookUser mapToUser(String body) throws IOException {
        return mapper.readValue(body, FacebookUser.class);
    }

}

