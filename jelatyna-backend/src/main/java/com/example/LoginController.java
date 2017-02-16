package com.example;

import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.User;
import com.example.user.UserRepository;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

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
        UserDto userDto = twitter.getUser(accessToken);
        User user = mapToSystemUser(userDto);
        return ResponseEntity.ok(asToken(user));

    }

    private User mapToSystemUser(UserDto userDto) {
        String id = "twitter_" + userDto.getId();
        if (!userRepository.exists(id)) {
            userRepository.save(new User()
                    .setId(id)
                    .setUsername(userDto.getUserName())
                    .setName(userDto.getName()));
        }
        return userRepository.getOne(id);
    }

    private String asToken(User user) {
        return Jwts.builder()
                .setClaims(new HashMap<String, Object>() {{
                    put("isAdmin", user.isAdmin());
                    put("username", user.getUsername());
                }})
                .setId(user.getId())
                .setSubject(user.getName())
                .setExpiration(Date.from(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.UTC)))
                .signWith(SignatureAlgorithm.HS512, MacProvider.generateKey()).compact();
    }

}
