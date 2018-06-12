package pl.confitura.jelatyna.login;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.confitura.jelatyna.infrastructure.fakedb.FakeDbConfig;
import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;

@RestController
@RequestMapping("/login/{provider}")
@CrossOrigin()
@Profile(FAKE_SECURITY)
public class FakeOAuth2LoginController {

    private TokenService tokenService;
    private FakeDbConfig fakeDbConfig;

    @Autowired
    public FakeOAuth2LoginController(TokenService tokenService, FakeDbConfig fakeDbConfig) {
        this.tokenService = tokenService;
        this.fakeDbConfig = fakeDbConfig;
    }

    @GetMapping()
    public ResponseEntity<Object> redirectToGitHubLogin(
            @PathVariable("provider") String provider
    ) {
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", "http://localhost:8080/login/" + provider)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithGitHub(
            @PathVariable("provider") String provider,
            @RequestParam("code") String code) {

        User user = fakeDbConfig.getBySystem(provider);
        if (user == null) {
            user = new User()
                    .setId("dHdpdHRlci9tYXJnaWVsbQ==")
                    .setName("Fake User " + provider);
        }
        return ResponseEntity.ok(tokenService.asToken(user));

    }

}
