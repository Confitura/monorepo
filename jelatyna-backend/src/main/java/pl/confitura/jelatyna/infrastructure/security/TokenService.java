package pl.confitura.jelatyna.infrastructure.security;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.user.User;

import javax.crypto.SecretKey;


@Service
@Slf4j
public class TokenService {

    private final SecretKey key = Jwts.SIG.HS256.key().build();

    public String asToken(User user) {
        log.info("Transforming user to token {}", user);
        return Jwts.builder()
                .claim("isAdmin", user.isAdmin())
                .claim("isVolunteer", user.isVolunteer())
                .claim("isSpeaker", user.isSpeaker())
                .claim("isNew", !user.getPrivacyPolicyAccepted())
                .id(user.getId())
                .subject(user.getName())
                .expiration(Date.from(LocalDateTime.now().plusHours(10).toInstant(ZoneOffset.UTC)))
                .signWith(getKey())
                .compact();
    }

    public JelatynaPrincipal toUser(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
                .parse(token).accept(new JwtHandlerAdapter<>() {

                    @Override
                    public JelatynaPrincipal onClaimsJws(Jws<Claims> jws) {
                        Claims body = jws.getPayload();
                        return new JelatynaPrincipal()
                                .setName(body.getSubject())
                                .setId(body.getId())
                                .setAdmin((Boolean) body.get("isAdmin"))
                                .setVolunteer((Boolean) body.get("isVolunteer"))
                                ;
                    }
                });
    }

    //TODO the keys shouldn't change after restart
    private SecretKey getKey() {
        return key;
    }
}
