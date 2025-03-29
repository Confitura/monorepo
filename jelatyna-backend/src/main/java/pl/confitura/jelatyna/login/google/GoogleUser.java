package pl.confitura.jelatyna.login.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.confitura.jelatyna.login.OAuthUserBase;
import pl.confitura.jelatyna.user.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by tj on 12.06.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class GoogleUser extends OAuthUserBase {
    private String displayName;
    private Optional<Image> image;
    private List<Email> emails;

    protected GoogleUser() {
        super(GoogleService.SYSTEM);
    }

    @Override
    protected User toUser() {
        String email = emails.stream()
                .filter(e -> e.type.equalsIgnoreCase("account"))
                .findFirst()
                .map(Email::value)
                .orElse(null);

        return new User()
                .setSocialId(encodeId())
                .setOrigin(getSystem())
                .setPhoto(image.map(Image::url).orElse(null))
                .setEmail(email)
                .setName(displayName);
    }

    record Image(String url) {
    }

    record Email(String value, String type) {
    }
}
