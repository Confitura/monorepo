package pl.confitura.jelatyna.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;

@RestController
@RequestMapping("/login/{provider}")
@CrossOrigin()
public class OAuth2LoginController {

    private TokenService tokenService;
    private Map<String, AbstractOAuth20Service> services;

    @Autowired
    public OAuth2LoginController(
            TokenService tokenService,
            Map<String, AbstractOAuth20Service> services) {
        this.tokenService = tokenService;
        this.services = services;
    }

    @GetMapping()
    public ResponseEntity<Object> redirectToGitHubLogin(
            @PathVariable("provider") String provider
    ) {
        AbstractOAuth20Service service = services.get(provider);
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", service.getAuthorizationUrl())
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithGitHub(
            @PathVariable("provider") String provider,
            @RequestParam("code") String code)
            throws InterruptedException, ExecutionException, IOException {
        User user = services.get(provider).getUserFor(code);
        return ResponseEntity.ok(tokenService.asToken(user));

    }

}
