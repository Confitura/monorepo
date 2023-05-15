package pl.confitura.jelatyna.allegro;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.allegro.adapter.AllegroClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AllegroVoucherSender {

    private final AllegroClient allegroClient;

    private final AllegroVoucherMessageRepository repository;


    @Async
    public void send() {
        List<AllegroVoucherMessage> all = repository.findAllBySendDateIsNull();
        for (AllegroVoucherMessage message : all) {
            try {
                sendMessage(message);
                Thread.sleep(333);
            } catch (Exception ex) {
                log.warn("sending message to {} failed", message.getBuyerLogin(), ex);
            }
        }
    }

    void sendMessage(AllegroVoucherMessage message) throws IOException, ExecutionException, InterruptedException {
        boolean sentSuccessful = allegroClient.sendMessage(message.getBuyerLogin(), message.getAllegroMessage());
        if (sentSuccessful) {
            allegroClient.markSent(message.getCheckoutFormId());
            message.sent();
            repository.save(message);
        } else {
            log.warn("sending message to {} failed", message.getBuyerLogin());
        }
    }
}
