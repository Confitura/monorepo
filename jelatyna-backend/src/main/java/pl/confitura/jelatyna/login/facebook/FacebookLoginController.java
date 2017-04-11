package pl.confitura.jelatyna.login.facebook;


import com.github.scribejava.core.model.OAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.login.OAuthUser;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;

@RestController
@RequestMapping("/login/facebook")
@CrossOrigin()
public class FacebookLoginController {

    private final FacebookService facebook;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Autowired
    public FacebookLoginController(
            FacebookService facebook,
            UserRepository userRepository,
            TokenService tokenService) {
        this.facebook = facebook;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @GetMapping()
    public ResponseEntity<Object> redirectToFacebookLogin() {
        String url = facebook.getAuthorizationUrl();
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header("Location", url)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> doLoginWithFacebook(String code) throws InterruptedException, ExecutionException, IOException {
        final OAuth2AccessToken accessToken = facebook.getAccessToken(code);
        OAuthUser userDto = facebook.getUser(accessToken);
        User user = mapToSystemUser(userDto);
        return ResponseEntity.ok(tokenService.asToken(user));
    }

    private User mapToSystemUser(OAuthUser userDto) {
        String id = "facebook_" + userDto.getId();
        if (!userRepository.exists(id)) {
            userRepository.save(new User()
                    .setId(id)
                    .setOrigin("facebook")
                    .setUsername(userDto.getUserName())
                    .setName(userDto.getName()));
        }
        return userRepository.findOne(id);
    }
}
