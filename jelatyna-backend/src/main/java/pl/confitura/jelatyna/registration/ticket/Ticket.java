package pl.confitura.jelatyna.registration.ticket;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "VALID_TOKENS")
public class Ticket {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    public Ticket() {
    }

    public Ticket(String id) {
        this.id = id;
    }
}
