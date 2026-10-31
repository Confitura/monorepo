package pl.confitura.jelatyna.conference.registration.mandrill;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillTemplate;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.conference.registration.domain.MailSender;
import pl.confitura.jelatyna.conference.registration.domain.MessageInfo;

@Service
@Slf4j
public class MandrillMailSender implements MailSender {
    private final MandrillApi api;
    private final MailConfigurationProperties properties;

    public MandrillMailSender(MailConfigurationProperties properties) {
        this.api = new MandrillApi(properties.getApiKey());
        this.properties = properties;
    }

    @Override
    public void send(MailType mailType, MessageInfo messageInfo) throws IOException {
        String template = mailType.templateName();
        try {
            send(template, messageInfo);
        } catch (MandrillApiError e) {
            // The error envelope (status, name, message, code), never the rendered mail.
            throw new IOException("Mandrill rejected template " + template + ": " + e.getMandrillErrorAsJson(), e);
        }
    }

    public void send(String template, MessageInfo messageInfo) throws MandrillApiError, IOException {
        String address = messageInfo.getEmail();
        log.info("Sending email of template {} to {}", template, messageInfo.maskedEmail());
        MandrillMessage message = new MandrillMessage();
        MandrillTemplate info = api.templates().info(template);
        message.setHtml(api.templates().render(template, ImmutableMap.of("content", "fake"), messageInfo.getVariables()));
        message.setFromEmail(properties.getFromEmail());
        message.setFromName(properties.getFromName());
        message.setTo(recipients(address));
        message.setSubject(info.getPublishSubject());
        addTicketIfAvailable(messageInfo, message);
        api.messages().send(message, false);
    }

    private void addTicketIfAvailable(MessageInfo messageInfo, MandrillMessage message) {
        if (messageInfo.hasTicket()) {
            message.setImages(Collections.singletonList(createTicketImage(messageInfo.getTicket())));
        }
    }

    private MandrillMessage.MessageContent createTicketImage(byte[] ticket) {
        MandrillMessage.MessageContent image = new MandrillMessage.MessageContent();
        image.setName("ticket");
        image.setType(MediaType.IMAGE_PNG_VALUE + "; name=\"ticket.png\"");
        image.setContent(Base64.getEncoder().encodeToString(ticket));
        return image;
    }

    private List<MandrillMessage.Recipient> recipients(String emailAddress) {
        MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
        recipient.setEmail(emailAddress);
        return Lists.newArrayList(recipient);
    }
}
