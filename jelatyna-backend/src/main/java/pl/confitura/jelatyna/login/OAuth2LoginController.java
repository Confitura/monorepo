package pl.confitura.jelatyna.login;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/login/{provider}")
@CrossOrigin()
@Profile("!" + FAKE_SECURITY)
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
    public ResponseEntity<Object> redirectToLogin(
            @PathVariable("provider") String provider,
            HttpServletRequest request
    ) {
        AbstractOAuth20Service service = services.get(provider);
        String callback = buildCallback(request, provider);
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", service.getAuthorizationUrl(callback))
                .build();
    }

    private String buildCallback(HttpServletRequest request, String provider) {
        String referer = request.getHeader("referer");
        if (isBlank(referer)) {
            return null;
        } else {
            return referer + "/" + provider;
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLogin(
            @PathVariable("provider") String provider,
            @RequestParam("code") String code) {
        User user = services.get(provider).getUserFor(code);
        return ResponseEntity.ok(tokenService.asToken(user));
    }

}
