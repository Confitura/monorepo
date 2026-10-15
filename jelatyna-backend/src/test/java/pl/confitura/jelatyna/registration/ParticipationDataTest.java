package pl.confitura.jelatyna.registration;

import org.junit.jupiter.api.Test;
import pl.confitura.jelatyna.registration.voucher.Voucher;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipationDataTest {

    @Test
    void toStringShouldDescribeProcessStateWithoutPersonalData() {
        ParticipationData data = new ParticipationData()
                .setId("participation-1")
                .setVoucher(new Voucher("voucher-1").setOriginalBuyer("buyer@example.com"))
                .setFirstName("Jan")
                .setLastName("Kowalski")
                .setEmail("jan@example.com")
                .setGender("female")
                .setSize("XL")
                .setInfo("gluten intolerance, wheelchair access")
                .setRegisteredBy("user-7")
                .setTicketSendDate(LocalDateTime.parse("2026-07-01T10:00:00"));

        assertThat(data.toString())
                .contains("participation-1")
                .contains("voucher-1")
                .contains("user-7")
                .contains("2026-07-01T10:00")
                .doesNotContain("Kowalski")
                .doesNotContain("jan@example.com")
                .doesNotContain("buyer@example.com")
                .doesNotContain("female")
                .doesNotContain("gluten intolerance")
                .doesNotContain("wheelchair");
    }
}
