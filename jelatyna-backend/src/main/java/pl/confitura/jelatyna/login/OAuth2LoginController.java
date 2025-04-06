package pl.confitura.jelatyna.login;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;

@RestController
@RequestMapping("/login/{provider}")
@CrossOrigin()
@Profile("!" + FAKE_SECURITY)
public class OAuth2LoginController {

    private final TokenService tokenService;
    private final Map<String, AbstractOAuth20Service> services;

    public OAuth2LoginController(TokenService tokenService, Map<String, AbstractOAuth20Service> services) {
        this.tokenService = tokenService;
        this.services = services;
    }

    @GetMapping()
    public ResponseEntity<Object> loginInProvider(
            @PathVariable("provider") String provider,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state
    ) {
        AbstractOAuth20Service service = services.get(provider);
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", service.getAuthorizationUrl(state, redirectUri))
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @PathVariable("provider") String provider,
            @RequestParam("state") String state,
            @RequestParam("code") String code) {
        User user = services.get(provider).getUserFor(code);
        String token = URLEncoder.encode(tokenService.asToken(user), StandardCharsets.UTF_8);
        String uri = "https://app.confitura.pl/login/" + provider + "?access_token=" + token + "&state=" + state;

        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", uri)
                .build();
    }

}
