package pl.confitura.jelatyna.conference.registration.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.identity.user.web.SecurityHelper.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.identity.user.web.SecurityHelper;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationData;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationRepository;
import pl.confitura.jelatyna.conference.registration.domain.VoucherService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

class ParticipationControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private VoucherService voucherService;

    @Test
    void aUserTakesATicketAndTheirProfileShowsIt() throws Exception {
        User owner = userRepository.save(new User().setName("Owner"));
        ParticipationData ticket = ticket();

        assign(ticket, owner, owner)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(owner.getId()))
                .andExpect(jsonPath("$.participant").value(true))
                .andExpect(jsonPath("$.speaker").value(false))
                .andExpect(jsonPath("$.participationData.id").value(ticket.getId()));
        mockMvc.perform(get("/current-user").with(user(owner)))
                .andExpect(jsonPath("$.participationData.id").value(ticket.getId()));
    }

    @Test
    void nobodyElseGivesAUserATicket() throws Exception {
        User owner = userRepository.save(new User().setName("Owner"));
        User other = userRepository.save(new User().setName("Other"));

        assign(ticket(), owner, other).andExpect(status().isForbidden());
        mockMvc.perform(get("/current-user").with(user(owner)))
                .andExpect(jsonPath("$.participant").value(false));
    }

    private ResultActions assign(ParticipationData ticket, User owner, User actor) throws Exception {
        return mockMvc.perform(post("/users/" + owner.getId() + "/participationData").with(user(actor))
                .contentType(APPLICATION_JSON).content("{\"id\":\"" + ticket.getId() + "\"}"));
    }

    private ParticipationData ticket() {
        // Only an admin issues vouchers.
        SecurityHelper.asAdmin();
        ParticipationData ticket = participationRepository.save(new ParticipationData().setVoucher(voucherService.generateVoucher("")));
        SecurityHelper.cleanSecurity();
        return ticket;
    }
}
