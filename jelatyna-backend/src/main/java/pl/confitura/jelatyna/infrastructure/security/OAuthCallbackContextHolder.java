package pl.confitura.jelatyna.infrastructure.security;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.Map;

import org.springframework.stereotype.Service;

import net.jodah.expiringmap.ExpiringMap;

@Service
public class OAuthCallbackContextHolder {
    private Map<String, OAuth2Context> tokenToContext = ExpiringMap.builder()
            .expiration(5, MINUTES)
            .build();

    public OAuth2Context getSecretFor(String token) {
        return tokenToContext.get(token);
    }

    public void setContext(String token, String secret, String state, String callback) {
        tokenToContext.put(token, new OAuth2Context(secret, state, callback));
    }

    public record OAuth2Context(String secret, String state, String callback) {
    }

}
