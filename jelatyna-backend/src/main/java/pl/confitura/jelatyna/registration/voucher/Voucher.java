package pl.confitura.jelatyna.registration.voucher;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;

import javax.persistence.*;

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
    private boolean emailSent;

    public Voucher() {
    }

    public Voucher(String id) {
        this.id = id;
    }
}
