package pl.confitura.jelatyna.agenda;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "agenda_day")
@Data
@Accessors(chain = true)
public class Day extends AuditedEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @NotNull
    private LocalDate date;

    @NotNull
    private String label;

    @NotNull
    private int displayOrder;
}