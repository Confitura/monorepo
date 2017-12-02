package pl.confitura.jelatyna.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

@RestController
@RequestMapping("/login/{provider}")
@CrossOrigin()
@Profile(FAKE_SECURITY)
public class FakeOAuth2LoginController {

    private TokenService tokenService;

    @Autowired
    public FakeOAuth2LoginController(TokenService tokenService) {
        this.tokenService = tokenService;
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
            @RequestParam("code") String code)
            throws InterruptedException, ExecutionException, IOException {
        User user = new User()
                .setId("dHdpdHRlci9tYXJnaWVsbQ==")
                .setName("Fake User " + provider)
                .setAdmin(true);
        return ResponseEntity.ok(tokenService.asToken(user));

    }

}
