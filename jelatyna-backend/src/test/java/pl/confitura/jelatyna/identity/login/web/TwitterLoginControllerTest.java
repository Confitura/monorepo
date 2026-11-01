package pl.confitura.jelatyna.identity.login.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import pl.confitura.jelatyna.platform.security.OAuthCallbackContextHolder;
import pl.confitura.jelatyna.identity.login.twitter.TwitterService;
import pl.confitura.jelatyna.identity.user.domain.TokenService;
import pl.confitura.jelatyna.identity.user.domain.User;

class TwitterLoginControllerTest {

    private final TwitterService twitter = mock(TwitterService.class);
    private final TokenService tokenService = mock(TokenService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new TwitterLoginController(new OAuthCallbackContextHolder(), twitter, tokenService))
            .build();

    @Test
    void returnsToTheAdminAppWhateverRedirectUriTheSignInNamed() throws Exception {
        OAuth1RequestToken requestToken = new OAuth1RequestToken("request-token", "request-secret");
        OAuth1AccessToken accessToken = new OAuth1AccessToken("access-token", "access-secret");
        User user = new User();
        when(twitter.getRequestToken()).thenReturn(requestToken);
        when(twitter.getAccessToken(eq(requestToken), eq("verifier"))).thenReturn(accessToken);
        when(twitter.getUser(accessToken)).thenReturn(user);
        when(tokenService.asToken(user)).thenReturn("the-token");

        mockMvc.perform(get("/login/twitter")
                        .param("redirect_uri", "https://elsewhere.example")
                        .param("state", "the state"))
                .andExpect(status().isPermanentRedirect())
                .andExpect(header().string("Location",
                        "https://api.twitter.com/oauth/authenticate?oauth_token=request-token"));

        mockMvc.perform(get("/login/twitter/callback")
                        .param("oauth_token", "request-token")
                        .param("oauth_verifier", "verifier"))
                .andExpect(status().isPermanentRedirect())
                .andExpect(header().string("Location",
                        "https://app.confitura.pl/login/twitter?state=the+state#access_token=the-token"));
    }

    // The request token is kept for 5 minutes only, so an expired one looks unknown.
    @Test
    void refusesACallbackWithAnUnknownRequestToken() throws Exception {
        mockMvc.perform(get("/login/twitter/callback")
                        .param("oauth_token", "unknown-token")
                        .param("oauth_verifier", "verifier"))
                .andExpect(status().isBadRequest());
    }
}
