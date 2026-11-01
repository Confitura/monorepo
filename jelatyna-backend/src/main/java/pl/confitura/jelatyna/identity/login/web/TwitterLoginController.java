package pl.confitura.jelatyna.identity.login.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static pl.confitura.jelatyna.platform.Profiles.FAKE_SECURITY;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import pl.confitura.jelatyna.platform.security.OAuthCallbackContextHolder;
import pl.confitura.jelatyna.identity.login.providers.twitter.TwitterService;
import pl.confitura.jelatyna.identity.user.domain.TokenService;

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

    // redirect_uri is still accepted so existing sign-in links work, but ignored: like the other
    // providers, X sign-in always returns to the admin app.
    @GetMapping
    public ResponseEntity<Object> redirectToTwitterLogin(
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state
    ) {
        OAuth1RequestToken token = twitter.getRequestToken();
        holder.setContext(token.getToken(), token.getTokenSecret(), state);
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", "https://api.twitter.com/oauth/authenticate?oauth_token=" + token.getToken())
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithTwitter(@RequestParam("oauth_token") String token,
                                                     @RequestParam("oauth_verifier") String verifier) {
        OAuthCallbackContextHolder.OAuth2Context context = holder.getSecretFor(token);
        // Unknown, or kept longer than the holder's 5 minutes.
        if (context == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Unknown or expired X sign-in");
        }
        OAuth1AccessToken accessToken = twitter.getAccessToken(new OAuth1RequestToken(token, context.secret()), verifier);
        String code = tokenService.asToken(twitter.getUser(accessToken));
        String state = URLEncoder.encode(context.state(), StandardCharsets.UTF_8);
        String callbackUrl = "https://app.confitura.pl/login/twitter?state=" + state + "#access_token=" + code;
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", callbackUrl)
                .build();
    }

}
