package pl.confitura.jelatyna.login;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pl.confitura.jelatyna.infrastructure.fakedb.FakeUsers;
import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/login/{provider}")
@Profile(FAKE_SECURITY)
@RequiredArgsConstructor
@CrossOrigin()
public class FakeOAuth2LoginController {
    private final TokenService tokenService;
    private final FakeUsers fakeUsers;


    @GetMapping
    public ResponseEntity<Object> redirectToGitHubLogin(
            @PathVariable("provider") String provider,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state
    ) {
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", "http://localhost:8080/api/login/" + provider + "/callback?" +
                                    "code=testCode" +
                                    "&state=" + state +
                                    "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithGitHub(
            @PathVariable("provider") String provider,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state
    ) {
        User user = getUser(provider);
        String token = URLEncoder.encode(tokenService.asToken(user), StandardCharsets.UTF_8);
        String uri = redirectUri +
                     "?access_token=" + token +
                     "&state=" + state;
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", uri)
                .build();

    }

    private User getUser(String provider) {
        User user = fakeUsers.getBySystem(provider);
        if (user == null) {
            user = new User()
                    .setId("dHdpdHRlci9tYXJnaWVsbQ==")
                    .setName("Fake User " + provider);
        }
        return user;
    }

}
