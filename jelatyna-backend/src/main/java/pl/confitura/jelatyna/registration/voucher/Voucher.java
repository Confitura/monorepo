package pl.confitura.jelatyna.registration.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Accessors(chain = true)
public class Voucher  extends AuditedEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @ToString.Include
    private String id;

    private String originalBuyer;
    private String comment;

    @Embedded
    private AllegroContext allegro;

    @ToString.Include
    private LocalDateTime ticketSendDate;

    @Enumerated(EnumType.STRING)
    @ToString.Include
    private VoucherType type;

    public boolean isEmailSent() {
        return ticketSendDate != null;
    }

    public Voucher() {
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
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


