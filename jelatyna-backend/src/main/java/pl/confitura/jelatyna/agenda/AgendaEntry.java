package pl.confitura.jelatyna.agenda;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.presentation.Presentation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Entity
@Table(
        name = "AGENDA",
        uniqueConstraints = @UniqueConstraint(columnNames = {"TIME_SLOT_ID", "ROOM_ID"})
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
    @JoinColumn(name = "TIME_SLOT_ID")
    private TimeSlot timeSlot;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    private String label;

    @OneToOne
    private Presentation presentation;
}
