package pl.confitura.jelatyna.agenda;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.user.dto.PublicUserDto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(
        name = "agenda",
        uniqueConstraints = @UniqueConstraint(columnNames = {"time_slot_id", "room_id"})
)
@Data
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

    public int getTimeSlotOrder() {
        return timeSlot.getDisplayOrder();
    }


    public Set<PublicUserDto> getSpeakers() {
        //TODO
        return Collections.emptySet();
    }
}
