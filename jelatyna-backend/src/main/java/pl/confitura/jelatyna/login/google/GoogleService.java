package pl.confitura.jelatyna.login.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import pl.confitura.jelatyna.login.AbstractOAuth20Service;
import pl.confitura.jelatyna.login.OAuthConfiguration;
import pl.confitura.jelatyna.login.OAuthUserService;

import java.io.IOException;

/**
 * @author tj
 * Date created 13-06-2017
 */

public class GoogleService extends AbstractOAuth20Service{
    public static final String SYSTEM = "google";

    private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/plus/v1/people/me";
    private static final String GOOGLE_SCOPE = "https://www.googleapis.com/auth/userinfo.email";

    public GoogleService(OAuthUserService oauthUserService, OAuthConfiguration.OAuthProviderProperties properties, ObjectMapper mapper) {
        super(oauthUserService, properties, mapper);
    }

    @Override
    protected OAuth20Service createService(OAuthConfiguration.OAuthProviderProperties properties) {
        return new ServiceBuilder(properties.getApiKey())
                .scope(GOOGLE_SCOPE)
                .responseType("code")
                .callback(properties.getCallback())
                .apiSecret(properties.getApiSecret())
                .build(GoogleApi20.instance());
    }

    @Override
    protected String getProtectedUserUrl() {
        return PROTECTED_RESOURCE_URL;
    }

    @Override
    protected GoogleUser mapToUser(final String body) throws IOException {
        return mapper.readValue(body, GoogleUser.class);
    }
}
