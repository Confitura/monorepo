package pl.confitura.jelatyna.login.twitter;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import pl.confitura.jelatyna.infrastructure.security.OAuthTokenHolder;
import pl.confitura.jelatyna.infrastructure.security.TokenService;

@RestController
@RequestMapping("/login/twitter")
@Profile("!" + FAKE_SECURITY)
public class TwitterLoginController {
    private OAuthTokenHolder holder;
    private TwitterService twitter;
    private TokenService tokenService;

    @Autowired
    public TwitterLoginController(OAuthTokenHolder holder, TwitterService twitter,
            TokenService tokenService) {
        this.holder = holder;
        this.twitter = twitter;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<Object> redirectToTwitterLogin() {
        OAuth1RequestToken token = twitter.getRequestToken();
        holder.setSecret(token.getToken(), token.getTokenSecret());
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", "https://api.twitter.com/oauth/authenticate?oauth_token=" + token.getToken())
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithTwitter(@RequestParam("oauth_token") String token,
            @RequestParam("oauth_verifier") String verifier) {
        OAuth1AccessToken accessToken = twitter.getAccessToken(new OAuth1RequestToken(token, holder.getSecretFor(token)), verifier);
        return ResponseEntity.ok(tokenService.asToken(twitter.getUser(accessToken)));
    }

}
