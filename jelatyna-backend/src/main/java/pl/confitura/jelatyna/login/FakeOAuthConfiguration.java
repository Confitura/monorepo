package pl.confitura.jelatyna.login;

import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.confitura.jelatyna.login.facebook.FacebookService;
import pl.confitura.jelatyna.login.github.GithubService;
import pl.confitura.jelatyna.login.google.GoogleService;
import pl.confitura.jelatyna.user.dto.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

@RequiredArgsConstructor
@Configuration
@Profile(FAKE_SECURITY)
public class FakeOAuthConfiguration {

    private final OAuthUserService oAuthUserService;

    @Bean("OAuthServices")
    public Map<String, AbstractOAuth20Service> services() {
        HashMap<String, AbstractOAuth20Service> map = new HashMap<>();
        map.put(GithubService.SYSTEM, fake(GithubService.SYSTEM));
        map.put(FacebookService.SYSTEM, fake(FacebookService.SYSTEM));
        map.put(GoogleService.SYSTEM, fake(GoogleService.SYSTEM));
        return map;
    }

    private AbstractOAuth20Service fake(String system) {
        return new AbstractOAuth20Service(null, null, null) {
            @Override
            protected OAuth20Service createService(OAuthConfiguration.OAuthProviderProperties properties) {
                return null;
            }

            @Override
            protected String getProtectedUserUrl() {
                return null;
            }

            @Override
            protected OAuthUserBase mapToUser(String body) throws IOException {
                return null;
            }

            @Override
            User getUserFor(String code) {
                return oAuthUserService.mapToUser(new OAuthUserBase(system) {

                    @Override
                    public String encodeId() {
                        return code;
                    }

                    @Override
                    protected User toUser() {
                        return new User()
                                .setSocialId(code)
                                .setOrigin(system)
                                .setName("User from " + system);
                    }

                });
            }

            @Override
            String getAuthorizationUrl() {
                return "http://localhost:8080/login/" + system + "?code=" + system;
            }
        };
    }

}
