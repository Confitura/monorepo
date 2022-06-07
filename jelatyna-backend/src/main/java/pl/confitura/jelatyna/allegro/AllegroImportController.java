package pl.confitura.jelatyna.allegro;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.allegro.adapter.AllegroClient;
import pl.confitura.jelatyna.allegro.adapter.dto.CheckoutForm;
import pl.confitura.jelatyna.allegro.adapter.dto.CheckoutForms;
import pl.confitura.jelatyna.registration.voucher.VoucherService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static pl.confitura.jelatyna.registration.voucher.Voucher.VoucherType.PARTICIPANT;

@RestController
@RequestMapping("allegro")
@RequiredArgsConstructor
class AllegroImportController {
    private final AllegroClient allegroClient;
    private final VoucherService voucherService;

    @GetMapping("status")
    Map<String, Object> getStatus() throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("isAuthorized", allegroClient.isAuthorized());
        return response;
    }

    @GetMapping("authorize")
    void authorize(HttpServletResponse httpServletResponse) throws IOException {
        String authorizationUrl = allegroClient.getAuthorizationUrl();
        httpServletResponse.sendRedirect(authorizationUrl);
    }

    @GetMapping("callback")
    String callback(@RequestParam String code, @RequestParam String state) {
        allegroClient.authorize(code, state);
        return "you are authorized. return to app";
    }

    @GetMapping(value = "report/ready-to-send", produces = {"text/csv"})
    String importFromAllegro() throws IOException, ExecutionException, InterruptedException {
        CheckoutForms readyForProcessing = allegroClient.getReadyForProcessing();
        return readyForProcessing.getCheckoutForms().stream()
                .map(this::createLine)
                .collect(Collectors.joining("\n"));

    }

    @PostMapping("import")
    void doImport() throws IOException, ExecutionException, InterruptedException {
        CheckoutForms readyForProcessing = allegroClient.getReadyForProcessing();
        for (CheckoutForm checkoutForm : readyForProcessing.getCheckoutForms()) {
            String email = checkoutForm.getBuyer().getEmail();
            String comment = "allegro id: " + checkoutForm.getId();

            for (int i = 0; i < checkoutForm.getQuantity(); i++) {
                voucherService.generateVoucherAndSend(email, PARTICIPANT, comment);
            }

            allegroClient.markSent(checkoutForm);
        }

    }

    @NotNull
    private String createLine(CheckoutForm it) {
        return it.getBuyer().getEmail()
                + ";" + it.getQuantity()
                + ";PARTICIPANT"
                + ";" + "allegro id: " + it.getId()
                ;
    }
}
