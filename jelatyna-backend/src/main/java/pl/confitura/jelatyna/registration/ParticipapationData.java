package pl.confitura.jelatyna.registration;

import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.registration.voucher.Voucher;

@Entity
@Data
@Accessors(chain = true)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_voucher", columnNames = {"voucher_id"})
})
public class ParticipapationData {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @OneToOne
    private Voucher voucher;

    private String city;
    private String gender;
    private String experience;
    private String role;
    private String size;
    private String info;
    private String createdBy;

    private LocalDateTime registrationDate;
    private LocalDateTime arrivalDate;
    private String registeredBy;
    private LocalDateTime ticketSendDate;
    private LocalDateTime surveySendDate;

    boolean alreadyArrived() {
        return this.arrivalDate != null;
    }

    boolean ticketNotSentYet() {
        return this.ticketSendDate == null;
    }

    boolean surveyNotSentYet() {
        return surveySendDate == null;
    }
}
