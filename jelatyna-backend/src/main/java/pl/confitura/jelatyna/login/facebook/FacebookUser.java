package pl.confitura.jelatyna.login.facebook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.confitura.jelatyna.login.OAuthUserBase;
import pl.confitura.jelatyna.user.User;

import static pl.confitura.jelatyna.login.facebook.FacebookService.SYSTEM;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookUser extends OAuthUserBase {
    private String name;

    protected FacebookUser() {
        super(SYSTEM);
    }

    @Override
    protected User toUser() {
        return new User()
                .setId(encodeId())
                .setOrigin(getSystem())
                .setName(name);
    }
}
