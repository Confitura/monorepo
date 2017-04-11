package pl.confitura.jelatyna.login.github;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;

@RestController
@RequestMapping("/login/github")
@CrossOrigin()
public class GitHubLoginController {

    private TokenService tokenService;
    private GitHubService service;

    @Autowired
    public GitHubLoginController(TokenService tokenService, GitHubService service) {
        this.tokenService = tokenService;
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<Object> redirectToGitHubLogin() {
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", service.getAuthorizationUrl())
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithGitHub(@RequestParam("code") String code)
            throws InterruptedException, ExecutionException, IOException {
        User user = service.getUserFor(code);
        return ResponseEntity.ok(tokenService.asToken(user));

    }

}
