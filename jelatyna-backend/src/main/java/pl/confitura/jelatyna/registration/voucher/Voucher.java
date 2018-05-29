package pl.confitura.jelatyna.registration.voucher;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Accessors(chain = true)
public class Voucher {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;


    private String createdBy;
    private String originalBuyer;
    private LocalDateTime creationDate;
    private boolean emailSent;

    public Voucher() {
    }

    public Voucher(String id) {
        this.id = id;
    }
}
