package pl.confitura.jelatyna.mail;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageInfoTest {

    @Test
    void shouldKeepFirstCharacterAndDomain() {
        assertThat(masked("email@example.com")).isEqualTo("e***@example.com");
    }

    @Test
    void shouldMaskSingleCharacterLocalPart() {
        assertThat(masked("a@example.com")).isEqualTo("a***@example.com");
    }

    @Test
    void shouldNotLeakAddressesItCannotParse() {
        assertThat(masked("not-an-address")).isEqualTo("***");
        assertThat(masked("@example.com")).isEqualTo("***");
        assertThat(masked(null)).isEqualTo("<no address>");
    }

    @Test
    void toStringShouldDescribeTheMessageWithoutItsSecrets() {
        MessageInfo info = new MessageInfo()
                .setEmail("email@example.com")
                .setName("Jan Kowalski")
                .setToken("f7c1-voucher-id");

        assertThat(info.toString())
                .contains("e***@example.com")
                .contains("hasTicket=false")
                .contains("token")
                .doesNotContain("email@example.com")
                .doesNotContain("Jan Kowalski")
                .doesNotContain("f7c1-voucher-id");
    }

    private String masked(String email) {
        return new MessageInfo().setEmail(email).maskedEmail();
    }
}
