package pl.confitura.jelatyna.agenda;

import jakarta.persistence.*;
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
@Table(name = "agenda")
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
    @JoinColumns({
            @JoinColumn(name = "time_slot_day_id"),
            @JoinColumn(name = "time_slot_display_order"),
    })
    private TimeSlot timeSlot;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String label;

    @ManyToOne
    private Presentation presentation;

    public int getTimeSlotOrder() {
        return timeSlot.getDisplayOrder();
    }

    public String getPresentationId() {
        return presentation == null ? null : presentation.getId();
    }


    public Set<PublicProfile> getSpeakers() {
        if (presentation == null || presentation.getSpeakers().isEmpty()) {
            return Collections.emptySet();
        } else {
            return presentation.getSpeakers().stream()
                    .map(PublicProfile::from)
                    .collect(toSet());
        }
    }

    public String getRoomId() {
        return room == null ? null : room.getId();
    }

    public boolean hasPresentation() {
        return presentation != null;
    }
}
