package pl.confitura.jelatyna.presentation.like;

import lombok.Data;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.presentation.Presentation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "presentation_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_like", columnNames = {"presentation_id", "token"})
        })
@Accessors(chain = true)
class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @NotEmpty
    @NotNull
    String token;

    @ManyToOne
    Presentation presentation;
}
