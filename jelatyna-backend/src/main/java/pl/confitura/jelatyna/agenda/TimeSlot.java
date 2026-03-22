package pl.confitura.jelatyna.agenda;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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

    @NotNull
    @EmbeddedId
    private TimeSlotId id;

    @Column(name = "start_time")
    LocalTime start;
    @Column(name = "end_time")
    LocalTime end;

    private boolean forAllRooms = false;

    @Transient
    private int timeSlotSpan = 1;


    public String getLabel() {
        return start.format(HOUR_FORMAT) + " - " + end.format(HOUR_FORMAT);
    }

    public int getDisplayOrder() {
        return id.displayOrder;
    }

    public TimeSlot setId(String dayId, int displayOrder) {
        this.id = new TimeSlotId(dayId, displayOrder);
        return this;
    }

    public TimeSlot mergeWith(TimeSlot that) {

        return new TimeSlot()
                .setId(new TimeSlot.TimeSlotId(this.getId().dayId(), this.getDisplayOrder()))
                .setStart(this.getStart())
                .setEnd(that.getEnd())
                .setTimeSlotSpan(this.getTimeSlotSpan() + that.getTimeSlotSpan());
    }

    @Embeddable
    public record TimeSlotId(
            @NotNull String dayId,
            @NotNull int displayOrder) {
    }
}
