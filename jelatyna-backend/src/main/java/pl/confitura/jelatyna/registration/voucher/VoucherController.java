package pl.confitura.jelatyna.registration.voucher;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
@RestController
class VoucherController {

    private final VoucherService voucherService;

    @GetMapping("/vouchers/{id}/check")
    ResponseEntity<String> canUseVoucher(@PathVariable("id") String voucherId) {
        if (!voucherService.isValid(voucherId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID");
        } else if (!voucherService.canUseVoucher(voucherId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("TAKEN");
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

    @Data
    static class GenerateVouchersRequest {
        private String email;
        private String comment;
        private int sponsorVouchers;
        private int participantVouchers;
    }
}
