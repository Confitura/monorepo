package pl.confitura.jelatyna.login;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.confitura.jelatyna.login.twitter.TwitterService;
import pl.confitura.jelatyna.infrastructure.security.TokenHolder;
import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;

@RestController
@RequestMapping("/login/")
@CrossOrigin()
public class LoginController {
    @Autowired
    private TokenHolder holder;
    @Autowired
    private TwitterService twitter;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;

    @GetMapping("twitter")
    public ResponseEntity<Object> login() {
        OAuth1RequestToken token = twitter.getRequestToken();
        holder.setSecret(token.getToken(), token.getTokenSecret());

        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", "https://api.twitter.com/oauth/authenticate?oauth_token=" + token.getToken())
                .build();
    }

    @GetMapping("twitter/callback")
    public ResponseEntity<String> hello(@RequestParam("oauth_token") String token, @RequestParam("oauth_verifier") String verifier) {
        OAuth1AccessToken accessToken = twitter.getAccessToken(new OAuth1RequestToken(token, holder.getSecretFor(token)), verifier);
        OAuthUser userDto = twitter.getUser(accessToken);
        User user = mapToSystemUser(userDto);
        return ResponseEntity.ok(tokenService.asToken(user));

    }

    private User mapToSystemUser(OAuthUser userDto) {
        String id = "twitter_" + userDto.getId();
        if (!userRepository.exists(id)) {
            userRepository.save(new User()
                    .setId(id)
                    .setUsername(userDto.getUserName())
                    .setName(userDto.getName()));
        }
        return userRepository.getOne(id);
    }



}
