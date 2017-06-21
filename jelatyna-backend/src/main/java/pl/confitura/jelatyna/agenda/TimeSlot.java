package pl.confitura.jelatyna.agenda;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "AGENDA_TIME_SLOT")
public class TimeSlot {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;
    String label;

    private boolean forAllRooms = false;

    @NotNull
    private int displayOrder;
}
