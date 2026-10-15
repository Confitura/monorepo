package pl.confitura.jelatyna.registration.voucher;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VoucherTest {

    @Test
    void toStringShouldNotExposeTheBuyer() {
        Voucher voucher = new Voucher("voucher-1")
                .setOriginalBuyer("buyer@example.com")
                .setComment("paid by transfer, phone 600100200")
                .setType(Voucher.VoucherType.PARTICIPANT)
                .setTicketSendDate(LocalDateTime.parse("2026-07-01T10:00:00"))
                .setAllegro(new Voucher.AllegroContext("auction-1", "Bilet Confitura 2026", "buyer_login_99"));

        assertThat(voucher.toString())
                .contains("voucher-1")
                .contains("PARTICIPANT")
                .contains("2026-07-01T10:00")
                .doesNotContain("buyer@example.com")
                .doesNotContain("600100200")
                .doesNotContain("buyer_login_99")
                .doesNotContain("Bilet Confitura");
    }
}
