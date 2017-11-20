package pl.confitura.jelatyna.login.google;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.user.User;

import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

@Service
@Profile(FAKE_SECURITY)
public class GoogleServiceFake implements GoogleService{
    @Override
    public String getAuthorizationUrl() {
        return "http://localhost:8080/login/google";
    }

    @Override
    public User getUserFor(String code) {
        return new User()
                .setId("google_id")
                .setAdmin(true)
                .setEmail("google@example.com")
                .setBio("from google")
                .setId("google");
    }
}
