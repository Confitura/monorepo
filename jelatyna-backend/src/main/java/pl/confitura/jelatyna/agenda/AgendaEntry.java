package pl.confitura.jelatyna.agenda;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(
        name = "agenda",
        uniqueConstraints = @UniqueConstraint(columnNames = { "time_slot_id", "room_id" })
)
@Data
@Accessors(chain = true)
public class AgendaEntry {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String label;

    private String presentationId;

    public int getTimeSlotOrder(){
        return timeSlot.getDisplayOrder();
    }

}
