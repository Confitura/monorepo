package pl.confitura.jelatyna.mail;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import pl.confitura.jelatyna.registration.TicketGenerator;

import static org.junit.jupiter.api.Assertions.fail;
import static pl.confitura.jelatyna.mail.MailSender.MailType.*;

@EnabledIfSystemProperty(named = "mandrillApiKey", matches = ".+")
class MailSenderTest {

    MailSender mailSender;
    TicketGenerator generator = new TicketGenerator();

    @BeforeEach
    void setUp() {
        if (System.getProperty("destinationMail") == null) {
            fail("set -DdestinationMail=your@email");
        }

        MailConfigurationProperties properties = new MailConfigurationProperties();
        properties.setApiKey(System.getProperty("mandrillApiKey"));
        properties.setFromEmail(System.getProperty("destinationMail"));
        properties.setFromName("Integration Test");
        mailSender = new MailSender(properties);

    }

    @Test
    void shouldSendTicket() {

        //given sample message
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setEmail(System.getProperty("destinationMail"));
        messageInfo.setToken("token");
        messageInfo.setTicket(generator.generateFor("token"));
        messageInfo.setName("Some Tester");

        try {
            //when TICKET is sent
            mailSender.send(TICKET, messageInfo);

        } catch (MandrillApiError e) {
            //then no Exception thrown

            fail("should not cause an exception; " + e.getMandrillErrorAsJson(), e);
        } catch (Exception e) {
            fail("should not cause an exception", e);
        }


    }

    @Test
    void shouldSendVoucher() {
        //given sample message
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setEmail(System.getProperty("destinationMail"));
        messageInfo.setToken("token");

        try {
            //when VOUCHER is sent
            mailSender.send(VOUCHER, messageInfo);

        } catch (MandrillApiError e) {
            //then no Exception thrown
            fail("should not cause an exception; " + e.getMandrillErrorAsJson(), e);
        } catch (Exception e) {
            fail("should not cause an exception", e);
        }
    }

    @Test
    void shouldSendSurvey() {
        //given sample message
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setEmail(System.getProperty("destinationMail"));
        messageInfo.setName("Some Tester");

        try {
            //when SURVEY is sent
            mailSender.send(SURVEY, messageInfo);

        } catch (MandrillApiError e) {
            //then no Exception thrown
            fail("should not cause an exception; " + e.getMandrillErrorAsJson(), e);
        } catch (Exception e) {
            fail("should not cause an exception", e);
        }
    }
}