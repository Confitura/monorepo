package pl.confitura.jelatyna.login.facebook;

import static pl.confitura.jelatyna.login.facebook.FacebookService.SYSTEM;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.confitura.jelatyna.login.OAuthUserBase;
import pl.confitura.jelatyna.user.dto.FullUserDto;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookUser extends OAuthUserBase {
    private String name;

    protected FacebookUser() {
        super(SYSTEM);
    }

    @Override
    protected FullUserDto toUser() {
        return new FullUserDto()
                .setSocialId(encodeId())
                .setOrigin(getSystem())
                .setName(name);
    }
}
