package pl.confitura.jelatyna.identity.login.providers.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.confitura.jelatyna.identity.login.domain.OAuthUserBase;
import pl.confitura.jelatyna.identity.user.domain.User;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubUser extends OAuthUserBase {
    private String name;
    private String login;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    private String bio;

    public GitHubUser() {
        super(GithubService.SYSTEM);
    }

    @Override
    public User toUser() {
        return new User()
                .setName(getName())
                .setGithub(login)
                .setPhoto(avatarUrl)
                .setBio(bio);

    }
}
