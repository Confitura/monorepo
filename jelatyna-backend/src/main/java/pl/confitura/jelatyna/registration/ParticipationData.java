package pl.confitura.jelatyna.registration;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;
import pl.confitura.jelatyna.registration.voucher.Voucher;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_voucher", columnNames = {"voucher_id"})
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ParticipationData extends AuditedEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @OneToOne(optional = false)
    private Voucher voucher;

    private String lastName;
    private String firstName;
    private String email;
    private boolean privacyPolicyAccepted;

    private String gender;
    private String size;
    private String info;

    private LocalDateTime arrivalDate;
    private String registeredBy;
    private LocalDateTime ticketSendDate;
    private LocalDateTime surveySendDate;

    boolean alreadyArrived() {
        return this.arrivalDate != null;
    }

    boolean surveyNotSentYet() {
        return surveySendDate == null;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
