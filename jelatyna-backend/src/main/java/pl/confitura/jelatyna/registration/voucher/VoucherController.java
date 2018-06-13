package pl.confitura.jelatyna.registration.voucher;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
