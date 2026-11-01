package pl.confitura.jelatyna.identity.user.web;

import java.time.Instant;

import pl.confitura.jelatyna.identity.user.domain.Login;

/** A Login as its User sees it: the provider and when it was added. */
public record LoginDto(String id, String provider, Instant createdDate) {

    static LoginDto from(Login login) {
        return new LoginDto(login.getId(), login.getProvider(), login.getCreatedDate());
    }
}
