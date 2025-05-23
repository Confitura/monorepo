package pl.confitura.jelatyna.registration.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Accessors(chain = true)
public class Voucher  extends AuditedEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String originalBuyer;
    private String comment;

    @Embedded
    private AllegroContext allegro;

    private LocalDateTime ticketSendDate;

    @Enumerated(EnumType.STRING)
    private VoucherType type;

    public boolean isEmailSent() {
        return ticketSendDate != null;
    }

    public Voucher() {
    }

    public Voucher(String id) {
        this.id = id;
    }

    public enum VoucherType {
        PARTICIPANT, SPEAKER, SPONSOR
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class AllegroContext{
        private String auctionId;
        private String auctionName;
        private String buyerLogin;
    }
}


