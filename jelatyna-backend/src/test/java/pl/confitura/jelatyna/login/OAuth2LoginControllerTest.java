package pl.confitura.jelatyna.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;

class OAuth2LoginControllerTest {

    @Test
    void encodesStateInRedirectLocation() {
        TokenService tokenService = mock(TokenService.class);
        AbstractOAuth20Service service = mock(AbstractOAuth20Service.class);
        User user = mock(User.class);
        when(service.getUserFor("code")).thenReturn(user);
        when(tokenService.asToken(user)).thenReturn("the-token");

        OAuth2LoginController controller =
                new OAuth2LoginController(tokenService, Map.of("google", service));

        // A crafted state that would break out of the query string if reflected verbatim.
        ResponseEntity<String> response = controller.callback("google", "a b&injected=1#frag", "code");

        String location = response.getHeaders().getFirst("Location");
        assertThat(location).isEqualTo(
                "https://app.confitura.pl/login/google?state=a+b%26injected%3D1%23frag#access_token=the-token");
    }
}
