package pl.confitura.jelatyna.login.facebook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import pl.confitura.jelatyna.login.OAuthUserBase;
import pl.confitura.jelatyna.user.User;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookUser extends OAuthUserBase {
    private String name;

    protected FacebookUser() {
        super("facebook");
    }

    @Override
    protected User toUser() {
        return new User()
                .setId(encodeId())
                .setOrigin(getSystem())
                .setName(name);
    }
}
