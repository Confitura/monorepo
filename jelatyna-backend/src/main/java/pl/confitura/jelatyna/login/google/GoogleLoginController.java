package pl.confitura.jelatyna.login.google;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;

/**
 * Created by tj on 12.06.17.
 */
@RestController
@RequestMapping("/login/google")
@CrossOrigin()
public class GoogleLoginController {
    private final GoogleService googleService;
    private final TokenService tokenService;

    @Autowired
    public GoogleLoginController(GoogleService googleService, TokenService tokenService) {
        this.googleService = googleService;
        this.tokenService = tokenService;
    }

    @GetMapping()
    public ResponseEntity<Object> redirectToGoogleLogin() {
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", googleService.getAuthorizationUrl())
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithGoogle(@RequestParam("code") String code)
            throws InterruptedException, ExecutionException, IOException {
        User user = googleService.getUserFor(code);
        return ResponseEntity.ok(tokenService.asToken(user));

    }
}
