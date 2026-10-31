package pl.confitura.jelatyna.conference.agenda.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.platform.persistence.AuditedEntity;

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
    @JoinColumns({
            @JoinColumn(name = "edition_id", referencedColumnName = "edition_id"),
            @JoinColumn(name = "day_id", referencedColumnName = "id"),
    })
    private Day day;
}
