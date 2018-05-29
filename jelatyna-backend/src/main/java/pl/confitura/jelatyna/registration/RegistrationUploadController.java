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
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.registration.voucher.VoucherService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static pl.confitura.jelatyna.registration.VoucherStatus.ERROR;
import static pl.confitura.jelatyna.registration.VoucherStatus.SUCCESS;

@RepositoryRestController
@Slf4j
@AllArgsConstructor
public class RegistrationUploadController {

    private MailSender sender;
    private VoucherService service;

    @PostMapping("/participants/upload")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<List<GenerateVouchersResponse>> upload(@RequestParam MultipartFile file, @AuthenticationPrincipal JelatynaPrincipal principal)
            throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()), ';');
        List<GenerateVouchersResponse> responses = stream(reader.spliterator(), false)
                .map(GenerateVouchersRequest::build)
                .map(registration -> sendVouchers(registration, principal))
                .collect(toList());
        return ResponseEntity.ok(responses);
    }

    private GenerateVouchersResponse sendVouchers(GenerateVouchersRequest registerRequest, JelatynaPrincipal principal) {
        String creatorName = principal.getName();
        long successCount = IntStream.range(0, registerRequest.count)
                .mapToObj((it) -> sendVouchers(registerRequest.buyerEmail, creatorName))
                .filter(SUCCESS::equals)
                .count();
        return new GenerateVouchersResponse(registerRequest, (int) successCount);

    }

    private VoucherStatus sendVouchers(String mail, String creatorName) {
        try {
            Voucher voucher = service.generateVoucher(mail, creatorName);
            sender.send("pre-registration", new MessageInfo().setEmail(mail).setToken(voucher.getId()));
            voucher.setEmailSent(true);
            return SUCCESS;
        } catch (Exception ex) {
            log.error("Error on sending email", ex);
            return ERROR;
        }
    }


    @Data
    @AllArgsConstructor
    static class GenerateVouchersRequest {
        private static final GenerateVouchersRequest ERR = new GenerateVouchersRequest("ERROR", -1);

        final String buyerEmail;
        final int count;

        static GenerateVouchersRequest build(String[] row) {
            try {
                String buyerEmail = row[0];
                int count = Integer.parseInt(row[1]);
                return new GenerateVouchersRequest(buyerEmail, count);
            } catch (Exception ex) {
                log.warn("unable to parse line for registration", ex);
                return ERR;
            }
        }
    }

    @Data
    static class GenerateVouchersResponse {
        String buyerEmail;
        int successCount;
        int requestedCount;

        GenerateVouchersResponse(GenerateVouchersRequest registerRequest, int successCount) {
            buyerEmail = registerRequest.buyerEmail;
            this.successCount = successCount;
            requestedCount = registerRequest.count;
        }
    }

}
