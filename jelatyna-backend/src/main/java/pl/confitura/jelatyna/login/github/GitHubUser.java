package pl.confitura.jelatyna.login.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import pl.confitura.jelatyna.login.OAuthUserBase;
import pl.confitura.jelatyna.user.User;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubUser extends OAuthUserBase {
    private String name;
    private String login;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    private String bio;

    public GitHubUser() {
        super("github");
    }

    @Override
    protected User toUser() {
        return new User()
                .setId(encodeId())
                .setOrigin(getSystem())
                .setName(getName())
                .setGithub(login)
                .setPhoto(avatarUrl)
                .setBio(bio);

    }
}
