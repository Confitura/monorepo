package pl.confitura.jelatyna.allegro;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.allegro.adapter.AllegroClient;
import pl.confitura.jelatyna.allegro.adapter.dto.CheckoutForm;
import pl.confitura.jelatyna.allegro.adapter.dto.CheckoutForms;
import pl.confitura.jelatyna.allegro.adapter.dto.Offer;
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.registration.voucher.VoucherService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AllegroImportService {
    private final AllegroClient allegroClient;
    private final VoucherService voucherService;
    private final AllegroVoucherMessageRepository allegroVoucherMessageRepository;
    private final AllegroVoucherSender voucherSender;

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
            if (allegroVoucherMessageRepository.existsByCheckoutFormId(checkoutForm.getId())) {
                continue;
            }
            Stream<Voucher> vouchers = createVouchers(checkoutForm);
            String allegroMessage = buildMessage(vouchers);
            String buyerLogin = checkoutForm.getBuyer().getLogin();
            AllegroVoucherMessage messageToSend = AllegroVoucherMessage.from(buyerLogin, allegroMessage, checkoutForm.getId());
            allegroVoucherMessageRepository.save(messageToSend);
        }

        voucherSender.send();
    }

    @NotNull
    private Stream<Voucher> createVouchers(CheckoutForm checkoutForm) {
        String buyerLogin = checkoutForm.getBuyer().getLogin();
        String email = checkoutForm.getBuyer().getEmail();
        String comment = "allegro id: " + checkoutForm.getId();
        Offer offer = checkoutForm.getLineItems().get(0).getOffer();
        String auctionId = offer.getId();
        String auctionName = offer.getName();

        return IntStream
                .range(0, checkoutForm.getQuantity().intValue())
                .mapToObj(it -> voucherService.generateVoucherFromAllegro(email, comment, buyerLogin, auctionId, auctionName));
    }

    @NotNull
    private String buildMessage(Stream<Voucher> vouchers) {
        String links = vouchers
                .map(it -> "https://2023.confitura.pl/participate?voucher=" + it.getId())
                .collect(Collectors.joining("\n"));

        return "Thanks for the support!\nTo register use link below:\n" + links;
    }

    public String getAuthorizationUrl() {
        return allegroClient.getAuthorizationUrl();
    }

    public boolean isAuthorized() {
        return allegroClient.isAuthorized();
    }
}
