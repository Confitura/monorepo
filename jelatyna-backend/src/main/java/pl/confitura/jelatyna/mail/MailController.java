package pl.confitura.jelatyna.mail;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/mailing")
@RequiredArgsConstructor
@Slf4j
class MailController {

    private final MailSender mailSender;

    @PreAuthorize("@security.isAdmin()")
    @PostMapping
    void sendMails(@RequestBody SendMailsRequest mailsRequest) {
        mailsRequest.getMessageInfoList().forEach(it -> doSend(mailsRequest, it));
    }

    @PreAuthorize("@security.isAdmin()")
    @GetMapping("/templates")
    List<String> getTemplates() throws IOException, MandrillApiError {
        return mailSender.getAvailableTemplates();
    }

    private void doSend(@RequestBody SendMailsRequest mailsRequest, MessageInfo it) {
        try {
            mailSender.send(mailsRequest.template, it);
        } catch (Exception e) {
            log.warn("Couldn't send mail {}", it, e);
        }
    }

    @Data
    public static class SendMailsRequest {
        String template;
        List<MessageInfo> messageInfoList;
    }
}
