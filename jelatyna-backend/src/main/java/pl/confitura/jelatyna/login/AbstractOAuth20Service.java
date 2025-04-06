package pl.confitura.jelatyna.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.user.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public abstract class AbstractOAuth20Service {

    protected final OAuth20Service auth20Service;
    private final OAuthUserService oauthUserService;
    protected final ObjectMapper mapper;
    private final String callback;

    public AbstractOAuth20Service(
            OAuthUserService oauthUserService,
            OAuthConfiguration.OAuthProviderProperties properties,
            ObjectMapper mapper) {
        this.auth20Service = createService(properties);
        this.oauthUserService = oauthUserService;
        this.mapper = mapper;
        this.callback = properties.getCallback();
    }

    protected abstract OAuth20Service createService(OAuthConfiguration.OAuthProviderProperties properties);

    protected String getAuthorizationUrl(String state, String redirectUri) {
        Map<String, String> additionalParams = Map.of(
                "redirect_uri", buildCallbackUri(redirectUri),
                "state", state);
        return auth20Service.getAuthorizationUrl(additionalParams);
    }

    User getUserFor(String code) {
        try {
            return doGetUser(code);
        } catch (Exception ex) {
            throw new RuntimeException("Error on fetching user", ex);
        }
    }

    private User doGetUser(String code)
            throws IOException, InterruptedException, ExecutionException {
        OAuth2AccessToken token = auth20Service.getAccessToken(code);
        return oauthUserService.mapToUser(getOAuthUserFor(token));
    }

    protected OAuthUserBase getOAuthUserFor(OAuth2AccessToken accessToken) throws InterruptedException, ExecutionException, IOException {
        final OAuthRequest request = new OAuthRequest(Verb.GET, getProtectedUserUrl());
        auth20Service.signRequest(accessToken, request);
        final Response response = auth20Service.execute(request);
        log.info("getOAuthUserFor {}", response.getBody());
        return mapToUser(response.getBody());
    }

    protected abstract String getProtectedUserUrl();

    protected abstract OAuthUserBase mapToUser(String body) throws IOException;

    public String buildCallbackUri(String redirectUri) {
        return callback;
    }
}
