package pl.confitura.jelatyna.agenda;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.user.PublicProfile;

import java.util.Collections;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

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

    @OneToOne
    private Presentation presentation;

    public int getTimeSlotOrder(){
        return timeSlot.getDisplayOrder();
    }


    public Set<PublicProfile> getSpeakers() {
        if (presentation == null || presentation.getSpeakers().isEmpty()) {
            return Collections.emptySet();
        } else {
            return presentation.getSpeakers().stream()
                    .map(PublicProfile::new)
                    .collect(toSet());
        }
    }
}
