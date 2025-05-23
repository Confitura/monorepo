package pl.confitura.jelatyna.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.confitura.jelatyna.login.facebook.FacebookService;
import pl.confitura.jelatyna.login.github.GithubService;
import pl.confitura.jelatyna.login.google.GoogleService;

import java.util.HashMap;
import java.util.Map;

import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

@EnableConfigurationProperties(OAuthConfiguration.OAuthConfigurationProperties.class)
@Configuration
@RequiredArgsConstructor
@Profile("!" + FAKE_SECURITY)
public class OAuthConfiguration {

    private final OAuthConfigurationProperties properties;

    private final OAuthUserService oauthUserService;

    private final ObjectMapper mapper;

    AbstractOAuth20Service github() {
        return new GithubService(
                oauthUserService,
                properties.oauth2.get(GithubService.SYSTEM),
                mapper);
    }

    AbstractOAuth20Service facebook() {
        return new FacebookService(
                oauthUserService,
                properties.oauth2.get(FacebookService.SYSTEM),
                mapper);
    }

    AbstractOAuth20Service google() {
        return new GoogleService(
                oauthUserService,
                properties.oauth2.get(GoogleService.SYSTEM),
                mapper);
    }

    @Bean("OAuthServices")
    public Map<String, AbstractOAuth20Service> services() {
        HashMap<String, AbstractOAuth20Service> map = new HashMap<>();
        map.put(GithubService.SYSTEM, github());
        map.put(FacebookService.SYSTEM, facebook());
        map.put(GoogleService.SYSTEM, google());
        return map;
    }


    @Data
    @ConfigurationProperties(prefix = "app.oauth")
    public static class OAuthConfigurationProperties {
        private Map<String, OAuthProviderProperties> oauth2 = new HashMap<>();
    }


    @Data
    public static class OAuthProviderProperties {
        private String apiKey;
        private String apiSecret;
        private String callback;
    }

}
