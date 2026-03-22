package pl.confitura.jelatyna.login.twitter;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/login/twitter")
@Profile(FAKE_SECURITY)
@RequiredArgsConstructor
@CrossOrigin()
public class FakeTwitterLoginController {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    private String userId;

    @GetMapping
    public ResponseEntity<Object> redirectToTwitterLogin(
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state) {
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", "http://localhost:8080/api/login/twitter/callback?" +
                                    "code=testCode" +
                                    "&state=" + state +
                                    "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithTwitter(
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state) {
        User user = getUser();
        String token = URLEncoder.encode(tokenService.asToken(user), StandardCharsets.UTF_8);
        String uri = redirectUri +
                     "?access_token=" + token +
                     "&state=" + state;
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", uri)
                .build();

    }

    private User getUser() {
        User user;
        if (userId == null) {
            user = userRepository.save(new User()
                    .setName("Fake User")
                    .setOrigin("twitter")
                    .setTwitter("fake_user")
                    .setBio("Fake bio"));
            userId = user.getId();
        } else {
            user = userRepository.findById(userId);
        }
        return user;
    }

}
