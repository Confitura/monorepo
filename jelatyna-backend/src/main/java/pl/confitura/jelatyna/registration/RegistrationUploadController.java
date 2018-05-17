package pl.confitura.jelatyna.registration;

import com.opencsv.CSVReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.mail.MailSender;
import pl.confitura.jelatyna.mail.MessageInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static pl.confitura.jelatyna.registration.RegistrationStatus.ERROR;
import static pl.confitura.jelatyna.registration.RegistrationStatus.SUCCESS;

@RepositoryRestController
@Slf4j
@AllArgsConstructor
public class RegistrationUploadController {

    private MailSender sender;
    private ParticipantRepository repository;

    @PostMapping("/participants/upload")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<List<RegisterResponse>> upload(@RequestParam MultipartFile file, @AuthenticationPrincipal JelatynaPrincipal principal)
            throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()), ';');
        List<RegisterResponse> responses = stream(reader.spliterator(), false)
                .map(RegisterRequest::build)
                .map(registration -> register(registration, principal))
                .collect(toList());
        return ResponseEntity.ok(responses);
    }

    private RegisterResponse register(RegisterRequest registerRequest, JelatynaPrincipal principal) {
        String creatorName = principal.getName();
        long successCount = IntStream.range(0, registerRequest.count)
                .mapToObj((it) -> registerUser(registerRequest.buyerEmail, creatorName))
                .filter(SUCCESS::equals)
                .count();
        return new RegisterResponse(registerRequest, (int) successCount);

    }

    private RegistrationStatus registerUser(String mail, String creatorName) {
        try {
            Participant participant =
                    repository.save(new Participant().setCreationDate(now()).setOriginalBuyer(mail).setCreatedBy(creatorName));
            sender.send("pre-registration", new MessageInfo().setEmail(mail).setToken(participant.getId()));
            participant.setEmailSent(true);
            return SUCCESS;
        } catch (Exception ex) {
            log.error("Error on sending email", ex);
            return ERROR;
        }
    }


    @Data
    @AllArgsConstructor
    static class RegisterRequest {
        private static final RegisterRequest ERR = new RegisterRequest("ERROR", -1);

        final String buyerEmail;
        final int count;

        static RegisterRequest build(String[] row) {
            try {
                String buyerEmail = row[0];
                int count = Integer.parseInt(row[1]);
                return new RegisterRequest(buyerEmail, count);
            } catch (Exception ex) {
                log.warn("unable to parse line for registration", ex);
                return ERR;
            }
        }
    }

    @Data
    static class RegisterResponse {
        String buyerEmail;
        int successCount;
        int requestedCount;

        RegisterResponse(RegisterRequest registerRequest, int successCount) {
            buyerEmail = registerRequest.buyerEmail;
            this.successCount = successCount;
            requestedCount = registerRequest.count;
        }
    }

}
