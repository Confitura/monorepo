package pl.confitura.jelatyna.login.facebook;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;

@RestController
@RequestMapping("/login/facebook")
@CrossOrigin()
public class FacebookLoginController {

    private final FacebookService facebook;
    private final TokenService tokenService;

    @Autowired
    public FacebookLoginController(
            FacebookService facebook,
            TokenService tokenService) {
        this.facebook = facebook;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<Object> redirectToFacebookLogin() {
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", facebook.getAuthorizationUrl())
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithFacebook(String code) throws InterruptedException, ExecutionException, IOException {
        User user = facebook.getUser(code);
        return ResponseEntity.ok(tokenService.asToken(user));
    }

}
