package pl.confitura.jelatyna.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
    private Voucher validVoucher;

    @BeforeEach
    void setup() {
        SecurityHelper.asAdmin();
        this.user = userRepository.save(new User().setName("user1"));
        validVoucher = voucherService.generateVoucher("buyer");
        SecurityHelper.cleanSecurity();

    }


    @Test
    void user_is_able_to_create_participantion() throws Exception {
        //when user tries to register as participationData
        mockMvc.perform(
                post("/participants")
                        .content(voucherJson(validVoucher))
                        .contentType(HAL_JSON))
                .andExpect(status().is2xxSuccessful());


        //then participationData is saved
        ParticipationData byVoucher = participationRepository.findByVoucher(validVoucher);
        assertThat(byVoucher).isNotNull();
    }

    private String voucherJson(Voucher voucher) {
        return "{\"voucher\":\"" + voucher.getId() + "\"}";
    }

    @Test
    void user_is_not_able_to_create_participation_without_voucher() throws Exception {
        mockMvc.perform(
                post("/participants")
                        .content("{}")
                        .contentType(HAL_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void should_not_create_if_voucher_already_used() throws Exception {
        //given voucher is already used
        ParticipationData saved = participationRepository.save(new ParticipationData().setVoucher(validVoucher));

        //when user tries to use same voucher
        mockMvc.perform(
                post("/participants")
                        .content(voucherJson(validVoucher))
                        .contentType(HAL_JSON))
                .andExpect(status().isConflict());

        //then voucher is assaigned to old user
        ParticipationData byVoucher = participationRepository.findByVoucher(validVoucher);
        assertThat(byVoucher).isEqualTo(saved);
    }

    @Test
    void should_not_accept_invalid_token() throws Exception {
        //when user tries to register with invalid voucher
        mockMvc.perform(
                post("/participants")
                        .content("{\"voucher\" :  \"invalid\"}")
                        .contentType(HAL_JSON))
                .andExpect(status().is4xxClientError());

        //then participationData is not registered

    }

    @Test
    void user_is_able_to_update_participation_data() throws Exception {
        //given user with participationData
        SecurityHelper.asAdmin();
        ParticipationData participationData = participationRepository.save(new ParticipationData().setVoucher(validVoucher));

        Voucher voucher = voucherService.generateVoucher("");
        SecurityHelper.cleanSecurity();

        //when user assigns token
        mockMvc.perform(
                put("/participants/" + participationData.getId())
                        .content(voucherJson(voucher))
                        .contentType(HAL_JSON))
                .andExpect(status().is2xxSuccessful());

        //then token is assigned to participationData
        ParticipationData found = participationRepository.findById(participationData.getId());
        assertThat(found.getVoucher()).isNotNull();
        assertThat(found.getVoucher().getId()).isEqualTo(voucher.getId());
    }

    @Test
    void user_cannot_assign_token_already_used() throws Exception {
        //given different user with participationData
        SecurityHelper.asAdmin();
        Voucher voucher = voucherService.generateVoucher("");
        ParticipationData otherParticipation = participationRepository.save(new ParticipationData().setVoucher(voucher));

        ParticipationData participationData = participationRepository.save(new ParticipationData().setVoucher(validVoucher));
        SecurityHelper.cleanSecurity();

        //when user assigns token
        mockMvc.perform(
                put("/participants/" + participationData.getId())
                        .content(voucherJson(voucher))
                        .contentType(HAL_JSON))
                .andExpect(status().isConflict());

        //then token is assigned to participationData
        ParticipationData found = participationRepository.findById(participationData.getId());
        assertThat(found.getVoucher()).isNotEqualTo(voucher);
    }
}
