package pl.confitura.jelatyna.identity.login.web;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.identity.user.domain.TokenService;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

class TokenControllerTest extends BaseIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.jwt.secret-key}")
    private String jwtSecretKey;

    @Test
    void aSignInTokenSaysThePersonAuthenticatedJustNow() {
        Instant before = Instant.now().truncatedTo(SECONDS);
        User user = userRepository.save(new User().setName("Signed in"));

        // The token every provider's sign-in callback hands out.
        Claims claims = claimsOf(tokenService.asToken(user));

        assertThat(Instant.ofEpochSecond(claims.get("auth_time", Long.class))).isBetween(before, Instant.now());
    }

    @Test
    void refreshingKeepsWhenThePersonSignedInAndMovesOnlyIssuedAtAndExpiry() throws Exception {
        User user = userRepository.save(new User().setName("Signed in"));
        Instant aMonthAgo = Instant.now().minus(Duration.ofDays(30)).truncatedTo(SECONDS);
        Date expiry = Date.from(aMonthAgo.plus(Duration.ofDays(365)));
        String old = Jwts.builder()
                .claim("isAdmin", false)
                .claim("isVolunteer", false)
                .claim("auth_time", aMonthAgo.getEpochSecond())
                .id(user.getId())
                .issuedAt(Date.from(aMonthAgo))
                .expiration(expiry)
                .signWith(serverKey())
                .compact();

        String refreshed = mockMvc.perform(post("/tokens/refresh-token").header(AUTHORIZATION, "Bearer " + old))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Claims claims = claimsOf(refreshed);
        assertThat(claims.get("auth_time", Long.class)).isEqualTo(aMonthAgo.getEpochSecond());
        assertThat(claims.getIssuedAt()).isAfter(Date.from(Instant.now().minus(Duration.ofMinutes(1))));
        assertThat(claims.getExpiration()).isAfter(expiry);
    }

    private Claims claimsOf(String token) {
        return Jwts.parser().verifyWith(serverKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey serverKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }
}
