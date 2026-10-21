package pl.confitura.jelatyna.user;

import java.time.Instant;

/** A Login as its User sees it: the provider and when it was added. */
public record LoginDto(String id, String provider, Instant createdDate) {

    static LoginDto from(Login login) {
        return new LoginDto(login.getId(), login.getProvider(), login.getCreatedDate());
    }
}
