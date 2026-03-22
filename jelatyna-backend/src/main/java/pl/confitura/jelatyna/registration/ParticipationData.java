package pl.confitura.jelatyna.registration;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.ToString;
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
@ToString(onlyExplicitlyIncluded = true)
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
    @ToString.Include
    private String id;

    @OneToOne(optional = false)
    @ToString.Include
    private Voucher voucher;

    private String lastName;
    private String firstName;
    private String email;
    @ToString.Include
    private boolean privacyPolicyAccepted;

    @ToString.Include
    private String gender;
    @ToString.Include
    private String size;
    @ToString.Include
    private String info;

    @ToString.Include
    private LocalDateTime arrivalDate;
    @ToString.Include
    private LocalDateTime registrationDate;
    @ToString.Include
    private String registeredBy;
    @ToString.Include
    private LocalDateTime ticketSendDate;
    @ToString.Include
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
