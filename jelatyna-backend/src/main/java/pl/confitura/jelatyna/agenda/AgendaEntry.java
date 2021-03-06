package pl.confitura.jelatyna.agenda;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.SpeakerEntity;
import pl.confitura.jelatyna.user.dto.PublicUserDto;

import java.util.Collections;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Entity
@Table(
        name = "agenda",
        uniqueConstraints = @UniqueConstraint(columnNames = { "time_slot_id", "room_id" })
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

    @OneToOne
    private Presentation presentation;

    public int getTimeSlotOrder(){
        return timeSlot.getDisplayOrder();
    }


    public Set<PublicUserDto> getSpeakers() {
        if (presentation == null || presentation.getSpeakers().isEmpty()) {
            return Collections.emptySet();
        } else {
            return presentation.getSpeakers().stream()
                    .map(SpeakerEntity::toPublicUser)
                    .collect(toSet());
        }
    }
}
