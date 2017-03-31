package pl.confitura.jelatyna.infrastructure.security;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import pl.confitura.jelatyna.user.User;

@Service
public class TokenService {
    private Key key = MacProvider.generateKey();

    public String asToken(User user) {
        return Jwts.builder()
                .setClaims(new HashMap<String, Object>() {{
                    put("isAdmin", user.isAdmin());
                    put("username", user.getUsername());
                }})
                .setId(user.getId())
                .setSubject(user.getName())
                .setExpiration(Date.from(LocalDateTime.now().plusSeconds(10).toInstant(ZoneOffset.UTC)))
                .signWith(SignatureAlgorithm.HS512, key).compact();
    }

    public JelatynaPrincipal toUser(String token) {
        return Jwts.parser().setSigningKey(key).parse(token, new JwtHandlerAdapter<JelatynaPrincipal>() {

            @Override
            public JelatynaPrincipal onClaimsJws(Jws<Claims> jws) {
                Claims body = jws.getBody();
                return new JelatynaPrincipal()
                        .setName(body.getSubject())
                        .setId(body.getId())
                        .setAdmin((Boolean) body.get("isAdmin"));
            }
        });
    }
}
