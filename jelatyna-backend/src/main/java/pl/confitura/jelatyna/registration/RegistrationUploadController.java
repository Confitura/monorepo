package pl.confitura.jelatyna.registration;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static pl.confitura.jelatyna.registration.VoucherStatus.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.mail.MailSender;
import pl.confitura.jelatyna.mail.MessageInfo;
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.registration.voucher.VoucherService;

@RepositoryRestController
@Slf4j
@AllArgsConstructor
public class RegistrationUploadController {

    private MailSender sender;
    private VoucherService service;

    @PostMapping("/participants/upload")
    @PreAuthorize("@security.isAdmin()")
    public ResponseEntity<List<GenerateVouchersResponse>> upload(@RequestParam MultipartFile file)
            throws IOException {
        List<GenerateVouchersResponse> responses =
                parseFile(file)
                .map(this::sendVouchers)
                .collect(toList());
        return ResponseEntity.ok(responses);
    }

    private Stream<GenerateVouchersRequest> parseFile(@RequestParam MultipartFile file) throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()), ';');
        return stream(reader.spliterator(), false)
                .map(GenerateVouchersRequest::build);
    }

    private GenerateVouchersResponse sendVouchers(GenerateVouchersRequest registerRequest) {
        long successCount = IntStream.range(0, registerRequest.count)
                .mapToObj(it -> registerRequest)
                .map(this::createVoucher)
                .map(this::sendVoucher)
                .filter(SUCCESS::equals)
                .count();
        return new GenerateVouchersResponse(registerRequest, (int) successCount);

    }

    @Transactional
    VoucherStatus sendVoucher(Voucher voucher) {
        try {
            sender.send("pre-registration", new MessageInfo().setEmail(voucher.getOriginalBuyer()).setToken(voucher.getId()));
            voucher.setTicketSendDate(now());
            service.save(voucher);
            return SUCCESS;
        } catch (Exception ex) {
            log.error("Error on sending email", ex);
            return ERROR;
        }
    }


    @Transactional
    Voucher createVoucher(GenerateVouchersRequest request) {
        return service.generateVoucher(request.getBuyerEmail(), request.getType(), request.getComment());
    }

    @Data
    @AllArgsConstructor
    static class GenerateVouchersRequest {
        private static final GenerateVouchersRequest ERR = new GenerateVouchersRequest("ERROR", -1, null, null);

        final String buyerEmail;
        final int count;
        final Voucher.VoucherType type;
        final String comment;

        static GenerateVouchersRequest build(String[] row) {
            try {
                String buyerEmail = row[0];
                int count = Integer.parseInt(row[1]);
                Voucher.VoucherType type = getVoucherType(row);
                String comment = getComment(row);
                return new GenerateVouchersRequest(buyerEmail, count, type, comment);
            } catch (Exception ex) {
                log.warn("unable to parse line for registration", ex);
                return ERR;
            }
        }

        private static String getComment(String[] row) {
            if (row.length > 3) {
                return row[3];
            }
            return null;
        }

        private static Voucher.VoucherType getVoucherType(String[] row) throws UnableToParseVoucher {
            if (row.length > 2) {
                return extractType(row[2]);
            }
            return Voucher.VoucherType.PARTICIPANT;
        }

        private static Voucher.VoucherType extractType(String s) throws UnableToParseVoucher {
            try {
                return Voucher.VoucherType.valueOf(s);
            } catch (Exception ex) {
                throw new UnableToParseVoucher("unable to parse as voucher type: " + s + ", " +
                        "available options: " + Arrays.toString(Voucher.VoucherType.values()), ex);
            }
        }
    }

    @Data
    static class GenerateVouchersResponse {
        String buyerEmail;
        int successCount;
        int requestedCount;
        Voucher.VoucherType type;
        String comment;

        GenerateVouchersResponse(GenerateVouchersRequest registerRequest, int successCount) {
            buyerEmail = registerRequest.buyerEmail;
            this.successCount = successCount;
            requestedCount = registerRequest.count;
            type = registerRequest.type;
            comment = registerRequest.comment;
        }
    }

    static class UnableToParseVoucher extends Exception {
        UnableToParseVoucher(String s, Throwable throwable) {
            super(s, throwable);
        }
    }

}
