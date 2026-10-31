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
import pl.confitura.jelatyna.identity.user.domain.Speakers;
import pl.confitura.jelatyna.platform.security.JelatynaPrincipal;
import pl.confitura.jelatyna.user.User;

import javax.crypto.SecretKey;


@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    // OpenID Connect's name for when the person authenticated, in seconds since the epoch. Unlike iat it
    // survives refreshing, so it proves a fresh sign-in (ADR-0003).
    private static final String AUTH_TIME = "auth_time";

    private final JwtProperties properties;
    private final Speakers speakers;

    /** A token for a person who has just signed in with a provider. */
    public String asToken(User user) {
        return asToken(user, Instant.now());
    }

    /** A token for a person who signed in at {@code authTime}; null for tokens issued before it was recorded. */
    public String asToken(User user, Instant authTime) {
        log.info("Transforming user to token {}", user);
        return Jwts.builder()
                .claim("isAdmin", user.isAdmin())
                .claim("isVolunteer", user.isVolunteer())
                .claim("isSpeaker", speakers.isSpeaker(user.getId()))
                .claim("isNew", !user.getPrivacyPolicyAccepted())
                .claim(AUTH_TIME, authTime == null ? null : authTime.getEpochSecond())
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
                                .setAuthTime(authTime(body))
                                ;
                    }
                });
    }

    /** The id of the User a genuine token names, provided the person signed in at most {@code maxAge} ago. */
    public Optional<String> userIdIfSignedInWithin(String token, Duration maxAge) {
        try {
            Claims claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
            Instant authTime = authTime(claims);
            if (authTime == null || authTime.isBefore(Instant.now().minus(maxAge))) {
                return Optional.empty();
            }
            return Optional.of(claims.getId());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static Instant authTime(Claims claims) {
        Long seconds = claims.get(AUTH_TIME, Long.class);
        return seconds == null ? null : Instant.ofEpochSecond(seconds);
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
