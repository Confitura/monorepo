package pl.confitura.jelatyna.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.registration.voucher.VoucherService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistrationControllerTest extends BaseIntegrationTest {

    @Autowired
    RegistrationController registrationController;

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    VoucherService voucherService;

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        SecurityHelper.asAdmin();
        this.user = userRepository.save(new User().setName("user1"));
        SecurityHelper.cleanSecurity();

    }

    @Test
    void only_logged_in_user_is_able_to_create_participant() throws Exception {
        //when user tries to register as participant
        mockMvc.perform(
                post("/participants")
                        .content("{}")
                        .contentType(HAL_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void user_is_able_to_create_participant() throws Exception {
        //when user tries to register as participant
        mockMvc.perform(
                post("/participants")
                        .with(SecurityHelper.user(user))
                        .content("{}")
                        .contentType(HAL_JSON))
                .andExpect(status().is2xxSuccessful());


        //then participant is registered and assigned to user
        User byId = userRepository.findById(user.getId());
        assertThat(byId.getParticipant()).isNotNull();
    }

    @Test
    void should_not_create_if_user_already_is_participant() throws Exception {
        //when user tries to register as participant
        Participant participant = participantRepository.save(new Participant());
        user.setParticipant(participant);
        userRepository.save(user);

        mockMvc.perform(
                post("/participants")
                        .with(SecurityHelper.user(user))
                        .content("{}")
                        .contentType(HAL_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void user_is_able_to_create_participant_with_voucher() throws Exception {
        SecurityHelper.asAdmin();
        Voucher voucher = voucherService.generateVoucher("buyer", "test");
        SecurityHelper.cleanSecurity();
        //when user tries to register as participant
        mockMvc.perform(
                post("/participants")
                        .with(SecurityHelper.user(user))
                        .content("{" +
                                "\"voucher\":" +
                                "\"" + voucher.getId() + "\"" +
                                "}")
                        .contentType(HAL_JSON))
                .andExpect(status().is2xxSuccessful());

        //then participant is registered and assigned to user
        User byId = userRepository.findById(user.getId());
        assertThat(byId.getParticipant()).isNotNull();
        assertThat(byId.getParticipant().getVoucher().getId())
                .isEqualTo(voucher.getId());
    }

    @Test
    void shouldNotAcceptInvalidToken() throws Exception {
        //when user tries to register with invalid voucher
        mockMvc.perform(
                post("/participants")
                        .with(SecurityHelper.user(user))
                        .content("{" +
                                "\"voucher\" :  \"invalid\"" +
                                "}")
                        .contentType(HAL_JSON))
                .andExpect(status().is4xxClientError());

        //then participant is not registered
        User byId = userRepository.findById(user.getId());
        assertThat(byId.getParticipant()).isNull();
    }

    @Test
    void user_is_able_to_assign_token_to_participant() throws Exception {
        //given user with participant
        SecurityHelper.asAdmin();
        Participant participant = participantRepository.save(new Participant());
        userRepository.save(user.setParticipant(participant));

        Voucher voucher = voucherService.generateVoucher("", "");
        SecurityHelper.cleanSecurity();

        //when user assigns token
        mockMvc.perform(
                put("/participants/" + participant.getId())
                        .with(SecurityHelper.user(user))
                        .content("{" +
                                "\"voucher\" :  \"" + voucher.getId() + "\"" +
                                "}")
                        .contentType(HAL_JSON))
                .andExpect(status().is2xxSuccessful());

        //then token is assigned to participant
        Participant found = participantRepository.findById(participant.getId());
        assertThat(found.getVoucher()).isNotNull();
        assertThat(found.getVoucher().getId()).isEqualTo(voucher.getId());
    }

    @Test
    void user_cannot_assign_token_to_other_participant() throws Exception {
        //given different user with participant
        SecurityHelper.asAdmin();
        Participant participant = participantRepository.save(new Participant());
        User otherUser = userRepository.save(new User().setParticipant(participant));
        Voucher voucher = voucherService.generateVoucher("", "");
        SecurityHelper.cleanSecurity();

        //when user assigns token
        mockMvc.perform(
                put("/participants/" + participant.getId())
                        .with(SecurityHelper.user(user))
                        .content("{" +
                                "\"voucher\" :  \"" + voucher.getId() + "\"" +
                                "}")
                        .contentType(HAL_JSON))
                .andExpect(status().isForbidden());
        //then token is assigned to participant

        Participant found = participantRepository.findById(participant.getId());
        assertThat(found.getVoucher()).isNull();
    }

    @Test
    void user_is_cannot_assign_token_already_used() throws Exception {
        //given different user with participant
        SecurityHelper.asAdmin();
        Voucher voucher = voucherService.generateVoucher("", "");
        Participant otherParticipant = participantRepository.save(new Participant().setVoucher(voucher));
        User otherUser = userRepository.save(new User().setParticipant(otherParticipant));

        Participant participant = participantRepository.save(new Participant());
        userRepository.save(user.setParticipant(participant));
        SecurityHelper.cleanSecurity();

        //when user assigns token
        mockMvc.perform(
                put("/participants/" + participant.getId())
                        .with(SecurityHelper.user(user))
                        .content("{" +
                                "\"voucher\" :  \"" + voucher.getId() + "\"" +
                                "}")
                        .contentType(HAL_JSON))
                .andExpect(status().isConflict());

        //then token is assigned to participant
        Participant found = participantRepository.findById(participant.getId());
        assertThat(found.getVoucher()).isNull();
    }
}