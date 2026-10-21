package pl.confitura.jelatyna.infrastructure.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

class JwtAuthenticationFilterTest extends BaseIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void acceptsTheTokenOfAnExistingUser() throws Exception {
        User user = userRepository.save(new User().setName("Still Here"));

        mockMvc.perform(post("/tokens/refresh-token")
                        .header(AUTHORIZATION, "Bearer " + tokenService.asToken(user)))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsTheTokenOfADeletedUser() throws Exception {
        User user = userRepository.save(new User().setName("Gone"));
        String token = tokenService.asToken(user);
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", user.getId());

        mockMvc.perform(post("/tokens/refresh-token")
                        .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
