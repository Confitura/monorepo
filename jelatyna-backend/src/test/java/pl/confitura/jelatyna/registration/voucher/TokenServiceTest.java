package pl.confitura.jelatyna.registration.voucher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.registration.Participant;
import pl.confitura.jelatyna.registration.ParticipantRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class VoucherServiceTest extends BaseIntegrationTest {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherRepository voucherRepository;


    String creatorName = "creator";
    String mail = "test@example.com";

    @BeforeEach
    void setup() {
        SecurityHelper.asAdmin();
    }

    @Test
    void shouldGenerateVoucherAndStoreIt() {
        //when admin generates voucher
        Voucher voucher = voucherService.generateVoucher(mail, creatorName);

        //then voucher is stored in db
        assertThat(voucher).isNotNull();
        List<Voucher> storedVouchers = voucherRepository.findAll();
        assertThat(storedVouchers).contains(voucher);

    }

    @Test
    void generatedVoucherShouldBeValid() {
        //when admin generates voucher
        Voucher voucher = voucherService.generateVoucher(mail, creatorName);

        //then voucher should be recognized as valid
        assertThat(voucherService.isValid(voucher)).isTrue();
    }

    @Test
    void emptyVoucherShouldNotBeValid() {
        Voucher voucher = new Voucher();

        //when admin checks if empty voucher is valid
        boolean valid = voucherService.isValid(voucher);

        //then voucher should be recognized as invalid
        assertThat(valid).isFalse();

    }

    @Test
    void notGeneratedValueShouldNotBeValid() {
        Voucher voucher = new Voucher("id");

        //when admin checks if empty voucher is valid
        boolean valid = voucherService.isValid(voucher);

        //then voucher should be recognized as invalid
        assertThat(valid).isFalse();

    }

    @Test
    void shouldFindUnusedTokens() {
        // given there is unused token
        Voucher unused = voucherService.generateVoucher(mail, creatorName);
        // and used token
        Voucher used = voucherService.generateVoucher(mail, creatorName);
        createUserWithVoucher(used);

        // when finding unused tokens
        List<Voucher> unusedVouchers = voucherService.findUnusedVouchers();

        // then should only contain unused token
        assertThat(unusedVouchers)
                .isNotNull()
                .contains(unused)
                .doesNotContain(used);
    }

    @Autowired
    ParticipantRepository participantRepository;

    private void createUserWithVoucher(Voucher voucher) {
        participantRepository.save(new Participant().setVoucher(voucher));
    }
}