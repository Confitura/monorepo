package pl.confitura.jelatyna.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.login.facebook.FacebookService;
import pl.confitura.jelatyna.login.github.GithubService;
import pl.confitura.jelatyna.login.google.GoogleService;

import java.util.HashMap;
import java.util.Map;

@EnableConfigurationProperties(OAuthConfiguration.OAuthConfigurationProperties.class)
@Configuration
public class OAuthConfiguration {

    @Autowired
    private OAuthConfigurationProperties properties;

    @Autowired
    private OAuthUserService oauthUserService;

    @Autowired
    private ObjectMapper mapper;

    public OAuthConfiguration() {
        System.out.println();
    }

    private AbstractOAuth20Service github() {
        return new GithubService(
                oauthUserService,
                properties.oauth2.get(GithubService.SYSTEM),
                mapper);
    }

    private AbstractOAuth20Service facebook() {
        return new FacebookService(
                oauthUserService,
                properties.oauth2.get(FacebookService.SYSTEM),
                mapper);
    }

    private AbstractOAuth20Service google() {
        return new GoogleService(
                oauthUserService,
                properties.oauth2.get(GoogleService.SYSTEM),
                mapper);
    }

    @Bean("OAuthServices")
    public Map<String, AbstractOAuth20Service> services(){
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
