package pl.confitura.jelatyna.identity.login.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.identity.login.providers.GitHubUser;
import pl.confitura.jelatyna.identity.login.providers.TwitterUser;
import pl.confitura.jelatyna.identity.user.domain.Login;
import pl.confitura.jelatyna.identity.user.domain.LoginRepository;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthUserServiceTest extends BaseIntegrationTest {

    @Autowired
    private OAuthUserService oauthUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Test
    void signInReturnsTheUserThatOwnsTheLogin() {
        String githubId = uniqueId();
        User owner = userRepository.save(new User().setName("Manual speaker"));
        loginRepository.save(new Login(owner, "github", githubId));

        User signedIn = oauthUserService.mapToUser(gitHubUser(githubId, "Someone"));

        assertThat(signedIn.getId()).isEqualTo(owner.getId());
        assertThat(signedIn.getName()).isEqualTo("Manual speaker");
    }

    @Test
    void anUnknownLoginCreatesAUserAndItsLogin() {
        String githubId = uniqueId();

        User created = oauthUserService.mapToUser(gitHubUser(githubId, "Octo Cat"));

        assertThat(created.getName()).isEqualTo("Octo Cat");
        assertThat(loginRepository.findByProviderAndExternalId("github", githubId))
                .hasValueSatisfying(login -> assertThat(login.getUser().getId()).isEqualTo(created.getId()));
        assertThat(oauthUserService.mapToUser(gitHubUser(githubId, "Octo Cat")).getId())
                .isEqualTo(created.getId());
    }

    @Test
    void twitterLoginsAreKeyedByUsername() {
        String username = "confitura_" + uniqueId();
        TwitterUser twitterUser = new TwitterUser();
        twitterUser.setId("1234567");
        twitterUser.setUserName(username);

        User created = oauthUserService.mapToUser(twitterUser);

        assertThat(loginRepository.findByProviderAndExternalId("twitter", username))
                .hasValueSatisfying(login -> assertThat(login.getUser().getId()).isEqualTo(created.getId()));
    }

    private static GitHubUser gitHubUser(String id, String name) {
        GitHubUser user = new GitHubUser();
        user.setId(id);
        user.setName(name);
        return user;
    }

    private static String uniqueId() {
        return UUID.randomUUID().toString();
    }
}
