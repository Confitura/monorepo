package pl.confitura.jelatyna.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.BaseIntegrationTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthServiceTest extends BaseIntegrationTest {

    @Autowired
    private OAuthConfiguration.OAuthConfigurationProperties properties;

    @Autowired
    private OAuthUserService oauthUserService;

    @Autowired
    private ObjectMapper mapper;
    @BeforeEach
    public void setup() {
        OAuthConfiguration configuration = new OAuthConfiguration(properties, oauthUserService, mapper);
        google = configuration.google();
        github = configuration.github();
    }

    private AbstractOAuth20Service google;
    private AbstractOAuth20Service github;


    @Test
    void shouldParseGoogleUser() throws IOException {
        //given
        String body = """
                {
                  "id": "1234567890",
                  "image": {
                    "url": "https://2025.confitura.pl/_nuxt/logo_white.CozqRHby.svg"
                  },
                  "emails": [
                    {
                      "value": "example@confitura.pl",
                      "type": "ACCOUNT"
                    }
                  ],
                  "kind": "plus#person",
                  "etag": "%SDCFVGTRDFCVGFREDCV"
                }
                """;

        //when
        var googleUser = google.mapToUser(body);

        //then
        var user = googleUser.toUser();
        assertThat(user.getEmail()).isEqualTo("example@confitura.pl");
        assertThat(user.getPhoto()).isEqualTo("https://2025.confitura.pl/_nuxt/logo_white.CozqRHby.svg");
        assertThat(user.getOrigin()).isEqualTo("google");
        assertThat(user.getSocialId()).isEqualTo("Z29vZ2xlLzEyMzQ1Njc4OTA=");
    }

    @Test
    void shouldParseGithubUser() throws IOException {
        //given
        String body = """
                {
                  "login": "confitura_user",
                  "id": 1219657,
                  "node_id": "SAXCFREDSCFV=",
                  "avatar_url": "https://avatars.githubusercontent.com/u/@#$%^%$#@?v=4",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/confitura_user",
                  "html_url": "https://github.com/confitura_user",
                  "followers_url": "https://api.github.com/users/confitura_user/followers",
                  "following_url": "https://api.github.com/users/confitura_user/following{/other_user}",
                  "gists_url": "https://api.github.com/users/confitura_user/gists{/gist_id}",
                  "starred_url": "https://api.github.com/users/confitura_user/starred{/owner}{/repo}",
                  "subscriptions_url": "https://api.github.com/users/confitura_user/subscriptions",
                  "organizations_url": "https://api.github.com/users/confitura_user/orgs",
                  "repos_url": "https://api.github.com/users/confitura_user/repos",
                  "events_url": "https://api.github.com/users/confitura_user/events{/privacy}",
                  "received_events_url": "https://api.github.com/users/confitura_user/received_events",
                  "type": "User",
                  "user_view_type": "public",
                  "site_admin": false,
                  "name": "Confitura",
                  "company": null,
                  "blog": "",
                  "location": null,
                  "email": null,
                  "hireable": null,
                  "bio": null,
                  "twitter_username": null,
                  "notification_email": null,
                  "public_repos": 8,
                  "public_gists": 7,
                  "followers": 3,
                  "following": 0,
                  "created_at": "2011-11-25T08:18:58Z",
                  "updated_at": "2025-03-21T19:21:49Z"
                }
                """;
        //when
        var githubUser = github.mapToUser(body);

        //then
        var user = githubUser.toUser();
        assertThat(user.getEmail()).isEqualTo(null);
        assertThat(user.getPhoto()).isEqualTo("https://avatars.githubusercontent.com/u/@#$%^%$#@?v=4");
        assertThat(user.getOrigin()).isEqualTo("github");
        assertThat(user.getSocialId()).isEqualTo("Z2l0aHViLzEyMTk2NTc=");
    }
}