package pl.confitura.jelatyna.conference.registration.domain;

import java.io.IOException;

/** Sends registration's templated mails; the e-mail provider stays behind it. */
public interface MailSender {

    void send(MailType mailType, MessageInfo messageInfo) throws IOException;

    enum MailType {
        REGISTRATION_REMINDER("registration-reminder"), //TODO No template yet
        TICKET("registration-ticket"),
        SURVEY("survey"), // TODO add link to survey in template
        VOUCHER("pre-registration");

        private final String templateName;

        MailType(String templateName) {
            this.templateName = templateName;
        }

        public String templateName() {
            return templateName;
        }
    }
}
