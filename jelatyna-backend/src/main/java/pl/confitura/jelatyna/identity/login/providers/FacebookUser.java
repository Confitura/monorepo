package pl.confitura.jelatyna.identity.login.providers;

import static pl.confitura.jelatyna.identity.login.providers.FacebookService.SYSTEM;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.confitura.jelatyna.identity.login.domain.OAuthUserBase;
import pl.confitura.jelatyna.identity.user.domain.User;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookUser extends OAuthUserBase {
    private String name;

    protected FacebookUser() {
        super(SYSTEM);
    }

    @Override
    public User toUser() {
        return new User()
                .setName(name);
    }
}
