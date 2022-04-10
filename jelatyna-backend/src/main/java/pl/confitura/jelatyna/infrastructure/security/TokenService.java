package pl.confitura.jelatyna.infrastructure.security;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.user.User;

@Service
@Slf4j
public class TokenService {
    @Value("${jwt.key}")
    private String key;

    public String asToken(User user) {
        log.info("Transforming user to token {}", user);
        return Jwts.builder()
                .setClaims(new HashMap<String, Object>() {{
                    put("isAdmin", user.isAdmin());
                    put("isVolunteer", user.isVolunteer());
                    put("isSpeaker", user.isSpeaker());
                    put("isNew", StringUtils.isEmpty(user.getEmail()));
                }})
                .setId(user.getId())
                .setSubject(user.getName())
                .setExpiration(Date.from(LocalDateTime.now().plusHours(10).toInstant(ZoneOffset.UTC)))
                .signWith(SignatureAlgorithm.HS512, getKey()).compact();
    }

    public JelatynaPrincipal toUser(String token) {
        return Jwts.parser().setSigningKey(getKey()).parse(token, new JwtHandlerAdapter<JelatynaPrincipal>() {

            @Override
            public JelatynaPrincipal onClaimsJws(Jws<Claims> jws) {
                Claims body = jws.getBody();
                return new JelatynaPrincipal()
                        .setName(body.getSubject())
                        .setId(body.getId())
                        .setAdmin((Boolean) body.get("isAdmin"))
                        .setVolunteer((Boolean) body.get("isVolunteer"))
                        ;
            }
        });
    }

    private byte[] getKey() {
        return Base64.getEncoder().encode(key.getBytes());
    }
}
