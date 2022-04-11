package pl.confitura.jelatyna.registration.voucher;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.registration.ParticipationData;

@RequiredArgsConstructor
@RestController
class VoucherController {

    private final VoucherService voucherService;

    @GetMapping("/vouchers/{id}/check")
    ResponseEntity<RegistrationError> canUseVoucher(@PathVariable("id") String voucherId) {
        if (!voucherService.isValid(voucherId)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new RegistrationError().setReason("INVALID"));
        } else if (!voucherService.canUseVoucher(voucherId)) {
            String masked = getMaskedEmail(voucherId);
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new RegistrationError().setReason("TAKEN").setAdditionalInfo(masked));
        } else {
            return ResponseEntity.status(HttpStatus.OK).build();
        }

    }

    @GetMapping("/vouchers")
    @PreAuthorize("@security.isAdmin()")
    List<Voucher> findAll() {
        return voucherService.findAll();
    }

    @PostMapping("/vouchers")
    @PreAuthorize("@security.isAdmin()")
    List<Voucher> createVoucher(@RequestBody GenerateVouchersRequest request) {
        Stream<Voucher> sponsorVouchers = generateVouchers(request.sponsorVouchers, Voucher.VoucherType.SPONSOR, request);
        Stream<Voucher> participantVouchers = generateVouchers(request.participantVouchers, Voucher.VoucherType.PARTICIPANT, request);
        return Stream.concat(sponsorVouchers, participantVouchers)
                .collect(Collectors.toList());
    }

    private Stream<Voucher> generateVouchers(
            int numberOfVouchers,
            Voucher.VoucherType type,
            GenerateVouchersRequest request) {
        return IntStream.range(0, numberOfVouchers)
                .mapToObj(it -> voucherService.generateVoucher(request.email, type, request.comment));
    }

    private String getMaskedEmail(String voucherId) {
        ParticipationData data = voucherService.getParticipationDataFor(voucherId);
        String email = data.getEmail();
        return email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
    }

    @Data
    static class GenerateVouchersRequest {
        private String email;
        private String comment;
        private int sponsorVouchers;
        private int participantVouchers;
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    static class RegistrationError {
        private String reason;
        private String additionalInfo;
    }
}
