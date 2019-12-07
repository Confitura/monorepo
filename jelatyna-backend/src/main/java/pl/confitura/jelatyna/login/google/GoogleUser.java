package pl.confitura.jelatyna.login.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.confitura.jelatyna.login.OAuthUserBase;
import pl.confitura.jelatyna.user.dto.FullUserDto;

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
    protected FullUserDto toUser() {
        return new FullUserDto()
                .setSocialId(encodeId())
                .setOrigin(getSystem())
                .setName(displayName);
    }
}
