package pl.confitura.jelatyna.infrastructure.security;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.Map;

import org.springframework.stereotype.Service;

import net.jodah.expiringmap.ExpiringMap;

@Service
public class TokenHolder {
    private Map<String, String> tokenToSecret = ExpiringMap.builder()
            .expiration(5, MINUTES)
            .build();

    public String getSecretFor(String token) {
        return tokenToSecret.get(token);
    }

    public void setSecret(String token, String secret) {
        tokenToSecret.put(token, secret);
    }

}
