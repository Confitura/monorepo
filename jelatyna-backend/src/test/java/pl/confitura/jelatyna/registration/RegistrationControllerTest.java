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
    ParticipationRepository participationRepository;

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
    void only_logged_in_user_is_able_to_create_participantion() throws Exception {
        //when user tries to register as participapationData
        mockMvc.perform(
                post("/participants")
                        .content("{}")
                        .contentType(HAL_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void user_is_able_to_create_participantion() throws Exception {
        //when user tries to register as participapationData
        mockMvc.perform(
                post("/participants")
                        .with(SecurityHelper.user(user))
                        .content("{}")
                        .contentType(HAL_JSON))
                .andExpect(status().is2xxSuccessful());


        //then participapationData is registered and assigned to user
        User byId = userRepository.findById(user.getId());
        assertThat(byId.getParticipapationData()).isNotNull();
    }

    @Test
    void should_not_create_if_user_already_created_participantion() throws Exception {
        //when user tries to register as participapationData
        ParticipapationData participapationData = participationRepository.save(new ParticipapationData());
        user.setParticipapationData(participapationData);
        userRepository.save(user);

        mockMvc.perform(
                post("/participants")
                        .with(SecurityHelper.user(user))
                        .content("{}")
                        .contentType(HAL_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void user_is_able_to_create_participation_data_with_voucher() throws Exception {
        SecurityHelper.asAdmin();
        Voucher voucher = voucherService.generateVoucher("buyer", "test");
        SecurityHelper.cleanSecurity();
        //when user tries to register as participapationData
        mockMvc.perform(
                post("/participants")
                        .with(SecurityHelper.user(user))
                        .content("{" +
                                "\"voucher\":" +
                                "\"" + voucher.getId() + "\"" +
                                "}")
                        .contentType(HAL_JSON))
                .andExpect(status().is2xxSuccessful());

        //then participapationData is registered and assigned to user
        User byId = userRepository.findById(user.getId());
        assertThat(byId.getParticipapationData()).isNotNull();
        assertThat(byId.getParticipapationData().getVoucher().getId())
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

        //then participapationData is not registered
        User byId = userRepository.findById(user.getId());
        assertThat(byId.getParticipapationData()).isNull();
    }

    @Test
    void user_is_able_to_assign_token_to_participation_data() throws Exception {
        //given user with participapationData
        SecurityHelper.asAdmin();
        ParticipapationData participapationData = participationRepository.save(new ParticipapationData());
        userRepository.save(user.setParticipapationData(participapationData));

        Voucher voucher = voucherService.generateVoucher("", "");
        SecurityHelper.cleanSecurity();

        //when user assigns token
        mockMvc.perform(
                put("/participants/" + participapationData.getId())
                        .with(SecurityHelper.user(user))
                        .content("{" +
                                "\"voucher\" :  \"" + voucher.getId() + "\"" +
                                "}")
                        .contentType(HAL_JSON))
                .andExpect(status().is2xxSuccessful());

        //then token is assigned to participapationData
        ParticipapationData found = participationRepository.findById(participapationData.getId());
        assertThat(found.getVoucher()).isNotNull();
        assertThat(found.getVoucher().getId()).isEqualTo(voucher.getId());
    }

    @Test
    void user_cannot_assign_token_to_other_users_participation() throws Exception {
        //given different user with participapationData
        SecurityHelper.asAdmin();
        ParticipapationData participapationData = participationRepository.save(new ParticipapationData());
        User otherUser = userRepository.save(new User().setParticipapationData(participapationData));
        Voucher voucher = voucherService.generateVoucher("", "");
        SecurityHelper.cleanSecurity();

        //when user assigns token
        mockMvc.perform(
                put("/participants/" + participapationData.getId())
                        .with(SecurityHelper.user(user))
                        .content("{" +
                                "\"voucher\" :  \"" + voucher.getId() + "\"" +
                                "}")
                        .contentType(HAL_JSON))
                .andExpect(status().isForbidden());
        //then token is assigned to participapationData

        ParticipapationData found = participationRepository.findById(participapationData.getId());
        assertThat(found.getVoucher()).isNull();
    }

    @Test
    void user_is_cannot_assign_token_already_used() throws Exception {
        //given different user with participapationData
        SecurityHelper.asAdmin();
        Voucher voucher = voucherService.generateVoucher("", "");
        ParticipapationData otherParticipation = participationRepository.save(new ParticipapationData().setVoucher(voucher));
        User otherUser = userRepository.save(new User().setParticipapationData(otherParticipation));

        ParticipapationData participapationData = participationRepository.save(new ParticipapationData());
        userRepository.save(user.setParticipapationData(participapationData));
        SecurityHelper.cleanSecurity();

        //when user assigns token
        mockMvc.perform(
                put("/participants/" + participapationData.getId())
                        .with(SecurityHelper.user(user))
                        .content("{" +
                                "\"voucher\" :  \"" + voucher.getId() + "\"" +
                                "}")
                        .contentType(HAL_JSON))
                .andExpect(status().isConflict());

        //then token is assigned to participapationData
        ParticipapationData found = participationRepository.findById(participapationData.getId());
        assertThat(found.getVoucher()).isNull();
    }
}