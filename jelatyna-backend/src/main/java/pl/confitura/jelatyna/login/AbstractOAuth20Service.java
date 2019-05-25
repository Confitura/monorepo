package pl.confitura.jelatyna.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.user.User;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class AbstractOAuth20Service {

    private final OAuthConfiguration.OAuthProviderProperties properties;
    protected OAuth20Service auth20Service;
    private OAuthUserService oauthUserService;
    protected ObjectMapper mapper;

    @Autowired
    public AbstractOAuth20Service(
            OAuthUserService oauthUserService,
            OAuthConfiguration.OAuthProviderProperties properties,
            ObjectMapper mapper) {
        this.oauthUserService = oauthUserService;
        this.properties = properties;
        this.auth20Service = createService(properties);
        this.mapper = mapper;
    }

    protected abstract OAuth20Service createService(OAuthConfiguration.OAuthProviderProperties properties);

    protected OAuth20Service createService(String callback) {
        if (isBlank(callback)) {
            return auth20Service;
        } else {
            return createService(properties.withCallback(callback));
        }
    }

    String getAuthorizationUrl(String callback) {
        return createService(callback).getAuthorizationUrl();
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
        return mapToUser(response.getBody());
    }

    protected abstract String getProtectedUserUrl();

    protected abstract OAuthUserBase mapToUser(String body) throws IOException ;

}
