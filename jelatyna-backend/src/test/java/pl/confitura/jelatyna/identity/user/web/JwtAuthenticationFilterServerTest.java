package pl.confitura.jelatyna.identity.user.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import io.jsonwebtoken.Jwts;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.identity.user.domain.TokenService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

// A real server, because MockMvc applies neither the servlet filter order nor the servlet
// filter registrations these tests are about.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.cors.origins=http://admin.example")
class JwtAuthenticationFilterServerTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @MockitoSpyBean
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void aRejectedTokenStillCarriesCorsHeaders() throws Exception {
        // Without the header the browser hides the 401, so the admin app can't tell a rejected
        // token from a network failure and sign the user out.
        User user = userRepository.save(new User().setName("Gone"));
        String token = tokenService.asToken(user);
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", user.getId());

        HttpResponse<Void> response = refreshToken(token);

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).hasValue("http://admin.example");
    }

    @Test
    void anAuthenticatedRequestIsAuthenticatedOnce() throws Exception {
        // Each authentication reads the token and looks its User up, so twice would mean two lookups.
        User user = userRepository.save(new User().setName("Still Here"));
        String token = tokenService.asToken(user);

        HttpResponse<Void> response = refreshToken(token);

        assertThat(response.statusCode()).isEqualTo(200);
        verify(tokenService, times(1)).toUser(token);
    }

    @Test
    void aTokenSignedWithAnotherKeyIsRejected() throws Exception {
        // Like every token issued before the secret key is rotated: its User still exists.
        User user = userRepository.save(new User().setName("Signed Elsewhere"));
        String token = Jwts.builder().id(user.getId()).subject(user.getName())
                .signWith(Jwts.SIG.HS384.key().build())
                .compact();

        assertRejectedWithCorsHeaders(token);
    }

    @Test
    void aMalformedTokenIsRejected() throws Exception {
        assertRejectedWithCorsHeaders("not-a-token");
    }

    // On public endpoints too: the admin app signs the person out only on a 401.
    private void assertRejectedWithCorsHeaders(String token) throws Exception {
        for (HttpResponse<Void> response : List.of(refreshToken(token), send("GET", "/api/faq-entries", token))) {
            assertThat(response.statusCode()).isEqualTo(401);
            assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).hasValue("http://admin.example");
        }
    }

    private HttpResponse<Void> refreshToken(String token) throws Exception {
        return send("POST", "/api/tokens/refresh-token", token);
    }

    private HttpResponse<Void> send(String method, String path, String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Origin", "http://admin.example")
                .header("Authorization", "Bearer " + token)
                .method(method, HttpRequest.BodyPublishers.noBody())
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding());
    }
}
