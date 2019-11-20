package pl.confitura.jelatyna.login.twitter;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.dto.User;

@RestController
@RequestMapping("/login/twitter")
@Profile(FAKE_SECURITY)
@AllArgsConstructor
public class FakeTwitterLoginController {
    private TokenService tokenService;

    @GetMapping
    public ResponseEntity<Object> redirectToTwitterLogin() {
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", "http://localhost:8080/login/twitter")
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithTwitter(@RequestParam("oauth_token") String token,
            @RequestParam("oauth_verifier") String verifier) {
        return ResponseEntity.ok(tokenService.asToken(
                new User().setId("dHdpdHRlci9tYXJnaWVsbQ==").setName("Fake User").setAdmin(false)));
    }

}
