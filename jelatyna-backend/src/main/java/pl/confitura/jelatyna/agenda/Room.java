package pl.confitura.jelatyna.agenda;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Table(name = "agenda_room")
@Data
@Accessors(chain = true)
public class Room {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;

    String label;

    @NotNull
    private int displayOrder;
}
