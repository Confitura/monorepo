package pl.confitura.jelatyna.login.twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.confitura.jelatyna.login.OAuthUserBase;
import pl.confitura.jelatyna.identity.user.domain.User;

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
                .setName(name)
                .setTwitter(userName);
    }

    // Keyed by username, as before Logins existed, so a rename yields a new Login.
    @Override
    public String getExternalId() {
        return userName;
    }

}
