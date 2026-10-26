package pl.confitura.jelatyna.infrastructure.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;

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

    // Tokens live for months, so the roles they carry may be outdated by the next request.
    @Test
    void anAdminWhoseFlagIsClearedLosesAdminAccessWithTheSameToken() throws Exception {
        User user = userRepository.save(new User().setName("Former Admin").setAdmin(true));
        String bearer = "Bearer " + tokenService.asToken(user);
        mockMvc.perform(get("/faq-categories").header(AUTHORIZATION, bearer))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{id}/admin/false", user.getId()).with(admin()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/faq-categories").header(AUTHORIZATION, bearer))
                .andExpect(status().isForbidden());
    }

    @Test
    void aVolunteerWhoseFlagIsClearedLosesVolunteerAccessWithTheSameToken() throws Exception {
        User user = userRepository.save(new User().setName("Former Volunteer").setVolunteer(true));
        String bearer = "Bearer " + tokenService.asToken(user);
        // No such participant, so a volunteer gets past the role check to a 404 and changes nothing.
        mockMvc.perform(post("/participants/{id}/arrived", "nobody").header(AUTHORIZATION, bearer))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/users/{id}/volunteer/false", user.getId()).with(admin()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/participants/{id}/arrived", "nobody").header(AUTHORIZATION, bearer))
                .andExpect(status().isForbidden());
    }
}
