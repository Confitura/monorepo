package pl.confitura.jelatyna.login.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.confitura.jelatyna.login.OAuthUserBase;
import pl.confitura.jelatyna.user.dto.User;

/**
 * Created by tj on 12.06.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class GoogleUser extends OAuthUserBase {
    private String displayName;

    protected GoogleUser() {
        super(GoogleService.SYSTEM);
    }

    @Override
    protected User toUser() {
        return new User()
                .setSocialId(encodeId())
                .setOrigin(getSystem())
                .setName(displayName);
    }
}
