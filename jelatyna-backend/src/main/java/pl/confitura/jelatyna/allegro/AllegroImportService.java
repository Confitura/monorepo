package pl.confitura.jelatyna.allegro;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.allegro.adapter.AllegroClient;
import pl.confitura.jelatyna.allegro.adapter.dto.CheckoutForm;
import pl.confitura.jelatyna.allegro.adapter.dto.CheckoutForms;
import pl.confitura.jelatyna.allegro.adapter.dto.Offer;
import pl.confitura.jelatyna.registration.voucher.VoucherService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AllegroImportService {
    private final AllegroClient allegroClient;
    private final VoucherService voucherService;

    String getReadyToSendCsv() throws IOException, ExecutionException, InterruptedException {
        CheckoutForms readyForProcessing = allegroClient.getReadyForProcessing();
        return readyForProcessing.getCheckoutForms().stream()
                .map(this::createCsvLine)
                .collect(Collectors.joining("\n"));
    }

    @NotNull
    private String createCsvLine(CheckoutForm checkoutForm) {
        return checkoutForm.getBuyer().getEmail()
                + ";" + checkoutForm.getQuantity()
                + ";PARTICIPANT"
                + ";" + "allegro id: " + checkoutForm.getId()
                ;
    }

    void authorize(String code, String state) {
        allegroClient.authorize(code, state);
    }

    void createVouchersFromAuctions() throws IOException, ExecutionException, InterruptedException {
        CheckoutForms readyForProcessing = allegroClient.getReadyForProcessing();
        for (CheckoutForm checkoutForm : readyForProcessing.getCheckoutForms()) {
            String email = checkoutForm.getBuyer().getEmail();
            String comment = "allegro id: " + checkoutForm.getId();
            String buyerLogin = checkoutForm.getBuyer().getLogin();
            Offer offer = checkoutForm.getLineItems().get(0).getOffer();
            String auctionId = offer.getId();
            String auctionName = offer.getName();

            String allegroMessage = buildMessage(checkoutForm, email, comment, buyerLogin, auctionId, auctionName);
            allegroClient.sendMessage(buyerLogin, allegroMessage);
            allegroClient.markSent(checkoutForm);
        }

    }

    @NotNull
    private String buildMessage(CheckoutForm checkoutForm, String email, String comment, String buyerLogin, String auctionId, String auctionName) {
        String vouchers = IntStream.range(0, checkoutForm.getQuantity().intValue())
                .mapToObj(it -> voucherService.generateVoucherFromAllegro(email, comment, buyerLogin, auctionId, auctionName))
                .map(it -> "https://2023.confitura.pl/participate?voucher=" + it.getId())
                .collect(Collectors.joining("\n"));

        return "Thanks for the support!\nTo register use link below:\n" + vouchers;
    }

    public String getAuthorizationUrl() {
        return allegroClient.getAuthorizationUrl();
    }

    public boolean isAuthorized() {
        return allegroClient.isAuthorized();
    }
}
