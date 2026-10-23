package pl.confitura.jelatyna.agenda;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.edition.Edition;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "agenda_day")
@Data
@Accessors(chain = true)
@IdClass(Day.DayId.class)
public class Day extends AuditedEntity {
    @Id
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @NotNull
    private LocalDate date;

    @NotNull
    private String label;

    @NotNull
    private int displayOrder;

    // The Edition whose agenda this Day is (ADR-0004): the Current Edition when it was created.
    // Part of the key: a Day id is unique within its Edition, as every Edition uses the same ones.
    @Id
    @ManyToOne
    @NotNull
    @JsonIgnore
    private Edition edition;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayId implements Serializable {
        private String id;
        private String edition;
    }
}