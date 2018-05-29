package pl.confitura.jelatyna.agenda;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Table(name = "agenda_time_slot")
@Accessors(chain = true)
public class TimeSlot extends AuditedEntity {

    private static final DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;
    String label; //fallback for already created slots - to be removed after fixing data in db

    LocalTime start;
    LocalTime end;

    private boolean forAllRooms = false;

    @NotNull
    private int displayOrder;

    public String getLabel() {
        if (start == null || end == null) {
            return label;
        } else {
            return start.format(HOUR_FORMAT) + " - " + end.format(HOUR_FORMAT);
        }

    }
}
