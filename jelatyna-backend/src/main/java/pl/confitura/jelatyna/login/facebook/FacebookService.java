package pl.confitura.jelatyna.login.facebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import pl.confitura.jelatyna.login.AbstractOAuth20Service;
import pl.confitura.jelatyna.login.OAuthConfiguration;
import pl.confitura.jelatyna.login.OAuthUserService;

import java.io.IOException;
import java.util.Random;

public class FacebookService extends AbstractOAuth20Service{
    public static final String SYSTEM = "facebook";
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v2.8/me";

    public FacebookService(OAuthUserService oauthUserService, OAuthConfiguration.OAuthProviderProperties properties, ObjectMapper mapper) {
        super(oauthUserService, properties, mapper);
    }

    @Override
    protected OAuth20Service createService(OAuthConfiguration.OAuthProviderProperties config) {
        final String secretState = "secret" + new Random().nextInt(999_999);
        return new ServiceBuilder(config.getApiKey())
                .apiSecret(config.getApiSecret())
                .state(secretState)
                .callback(config.getCallback())
                .build(FacebookApi.instance());
    }

    @Override
    protected String getProtectedUserUrl() {
        return PROTECTED_RESOURCE_URL;
    }

    @Override
    protected FacebookUser mapToUser(String body) throws IOException {
        return mapper.readValue(body, FacebookUser.class);
    }

}

