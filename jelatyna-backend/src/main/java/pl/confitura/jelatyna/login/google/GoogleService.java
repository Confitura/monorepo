package pl.confitura.jelatyna.login.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.login.OAuthUserService;
import pl.confitura.jelatyna.user.User;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author tj
 * Date created 13-06-2017
 */
@Service
public class GoogleService {
    private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/plus/v1/people/me";

    private OAuth20Service service;
    private OAuthUserService oauthUserService;
    private ObjectMapper mapper;

    public GoogleService(final OAuthUserService oauthUserService, final GoogleConfigurationProperties properties, final ObjectMapper mapper) {
        this.service = new ServiceBuilder()
                .apiKey(properties.getApiKey())
                .apiSecret(properties.getApiSecret())
                .build(GoogleApi20.instance());

        this.oauthUserService = oauthUserService;
        this.mapper = mapper;
    }

    String getAuthorizationUrl() {
        return service.getAuthorizationUrl();
    }

    User getUserFor(final String code) {
        try {
            final OAuth2AccessToken accessToken = getAccessToken(code);
            return oauthUserService.mapToUser(doGetUser(accessToken));
        } catch (Exception e) {
            throw new RuntimeException("Exception while validating a user", e);
        }

    }

    private OAuth2AccessToken getAccessToken(final String code) {
        try {
            return service.getAccessToken(code);
        } catch (Exception e) {
            throw new RuntimeException("Exception while loading request token", e);
        }
    }

    private GoogleUser doGetUser(final OAuth2AccessToken accessToken) throws InterruptedException, ExecutionException, IOException {
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        final Response response = service.execute(request);
        return mapToUser(response.getBody());
    }

    private GoogleUser mapToUser(final String body) throws IOException {
        return mapper.readValue(body, GoogleUser.class);
    }
}
