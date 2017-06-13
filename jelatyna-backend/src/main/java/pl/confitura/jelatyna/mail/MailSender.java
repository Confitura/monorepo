package pl.confitura.jelatyna.mail;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    public void send(String emailAddress, String template, Map<String, String> variables) throws IOException, MandrillApiError {
        log.info("Sending email to {}", emailAddress);
        MandrillMessage message = new MandrillMessage();
        MandrillTemplate info = api.templates().info(template);
        message.setHtml(api.templates().render(template, ImmutableMap.of("a", "b"), variables));
        message.setFromEmail(properties.getFromEmail());
        message.setFromName(properties.getFromName());
        message.setTo(recipients(emailAddress));
        message.setSubject(info.getPublishSubject());
        api.messages().send(message, false);
    }

    private List<MandrillMessage.Recipient> recipients(String emailAddress) {
        MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
        recipient.setEmail(emailAddress);
        return Lists.newArrayList(recipient);
    }
}
