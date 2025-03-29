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
    public ResponseEntity<Object> redirectToTwitterLogin() {
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", "http://localhost:5173/login/twitter?code=testCode")
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithTwitter() {
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
        return ResponseEntity.ok(tokenService.asToken(user));
    }

}
