package pl.confitura.jelatyna.allegro;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

import static java.util.UUID.randomUUID;

@Getter
@Entity
public class AllegroVoucherMessage {

    @Id
    private String id;

    private String buyerLogin;

    @Column(length = 10000)
    private String allegroMessage;
    private String checkoutFormId;
    private LocalDateTime sendDate;

    public AllegroVoucherMessage(String id, String buyerLogin, String allegroMessage, String checkoutFormId) {
        this.id = id;
        this.buyerLogin = buyerLogin;
        this.allegroMessage = allegroMessage;
        this.checkoutFormId = checkoutFormId;
    }

    public AllegroVoucherMessage() {

    }

    public static AllegroVoucherMessage from(String buyerLogin, String allegroMessage, String checkoutFormId) {
        return new AllegroVoucherMessage(randomUUID().toString(), buyerLogin, allegroMessage, checkoutFormId);
    }

    public void sent() {
        sendDate = LocalDateTime.now();
    }
}
