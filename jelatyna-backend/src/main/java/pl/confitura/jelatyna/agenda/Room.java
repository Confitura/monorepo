package pl.confitura.jelatyna.agenda;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "agenda_room")
@Data
@Accessors(chain = true)
public class Room extends AuditedEntity {
    @Id
    @Column(columnDefinition = "varchar(100)")
    private String id;

    String label;

    @NotNull
    private int displayOrder;

    @ManyToOne
    @NotNull
    private Day day;
}
