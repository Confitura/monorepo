package pl.confitura.jelatyna.mail;

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

@Service
@Slf4j
public class MailSender {
    private final MandrillApi api;
    private final MailConfigurationProperties properties;

    public MailSender(MailConfigurationProperties properties) {
        this.api = new MandrillApi(properties.getApiKey());
        this.properties = properties;
    }

    public void send(String template, MessageInfo messageInfo) throws IOException, MandrillApiError {
        String address = messageInfo.getEmail();
        log.info("Sending email to {}", address);
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
