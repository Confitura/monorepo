package pl.confitura.jelatyna.login.twitter;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import pl.confitura.jelatyna.infrastructure.security.OAuthCallbackContextHolder;
import pl.confitura.jelatyna.infrastructure.security.TokenService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/login/twitter")
@Profile("!" + FAKE_SECURITY)
@RequiredArgsConstructor
public class TwitterLoginController {

    private final OAuthCallbackContextHolder holder;
    private final TwitterService twitter;
    private final TokenService tokenService;

    @GetMapping
    public ResponseEntity<Object> redirectToTwitterLogin(
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state
    ) {
        OAuth1RequestToken token = twitter.getRequestToken();
        holder.setContext(token.getToken(), token.getTokenSecret(), state, redirectUri);
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", "https://api.twitter.com/oauth/authenticate?oauth_token=" + token.getToken())
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithTwitter(@RequestParam("oauth_token") String token,
                                                     @RequestParam("oauth_verifier") String verifier) {
        OAuthCallbackContextHolder.OAuth2Context context = holder.getSecretFor(token);
        OAuth1AccessToken accessToken = twitter.getAccessToken(new OAuth1RequestToken(token, context.secret()), verifier);
        String code = tokenService.asToken(twitter.getUser(accessToken));
        String state = URLEncoder.encode(context.state(), StandardCharsets.UTF_8);
        String callbackUrl = context.callback().concat("?access_token=").concat(code).concat("&state=").concat(state);
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", callbackUrl)
                .build();
    }

}
