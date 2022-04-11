package pl.confitura.jelatyna.login.twitter;

import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.confitura.jelatyna.login.OAuthUserBase;
import pl.confitura.jelatyna.user.User;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class TwitterUser extends OAuthUserBase {

    private String name;
    @JsonProperty("screen_name")
    private String userName;

    public TwitterUser() {
        super("twitter");
    }

    @Override
    protected User toUser() {
        return new User()
                .setSocialId(encodeId())
                .setOrigin(getSystem())
                .setName(name)
                .setTwitter(userName);
    }

    @Override
    public String encodeId() {
        return Base64.getEncoder()
                .encodeToString((getSystem() + "/" + userName)
                        .getBytes());
    }

}
