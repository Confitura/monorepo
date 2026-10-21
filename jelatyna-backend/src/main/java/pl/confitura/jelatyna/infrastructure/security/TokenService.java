package pl.confitura.jelatyna.infrastructure.security;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtHandlerAdapter;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import pl.confitura.jelatyna.user.User;

import javax.crypto.SecretKey;


@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final JwtProperties properties;

    public String asToken(User user) {
        log.info("Transforming user to token {}", user);
        return Jwts.builder()
                .claim("isAdmin", user.isAdmin())
                .claim("isVolunteer", user.isVolunteer())
                .claim("isSpeaker", user.isSpeaker())
                .claim("isNew", !user.getPrivacyPolicyAccepted())
                .id(user.getId())
                .subject(user.getName())
                .issuedAt(new Date())
                .expiration(Date.from(LocalDateTime.now().plusMonths(12).toInstant(ZoneOffset.UTC)))
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

    /** The id of the User a genuine token names, provided it was issued at most {@code maxAge} ago. */
    public Optional<String> userIdIfIssuedWithin(String token, Duration maxAge) {
        try {
            Claims claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
            Date issuedAt = claims.getIssuedAt();
            if (issuedAt == null || issuedAt.toInstant().isBefore(Instant.now().minus(maxAge))) {
                return Optional.empty();
            }
            return Optional.of(claims.getId());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.secretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @ConfigurationProperties(prefix = "app.jwt")
    @Validated
    record JwtProperties(@NotBlank String secretKey) {
    }
}
