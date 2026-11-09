package pl.confitura.jelatyna.identity.login.providers.facebook;

import tools.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import pl.confitura.jelatyna.identity.login.providers.AbstractOAuth20Service;
import pl.confitura.jelatyna.identity.login.providers.ProvidersConfiguration;
import pl.confitura.jelatyna.identity.login.domain.OAuthUserService;

import java.io.IOException;

public class FacebookService extends AbstractOAuth20Service{
    public static final String SYSTEM = "facebook";
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v2.8/me";

    public FacebookService(OAuthUserService oauthUserService, ProvidersConfiguration.OAuthProviderProperties properties, ObjectMapper mapper) {
        super(oauthUserService, properties, mapper);
    }

    @Override
    protected OAuth20Service createService(ProvidersConfiguration.OAuthProviderProperties config) {
        return new ServiceBuilder(config.getApiKey())
                .apiSecret(config.getApiSecret())
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

