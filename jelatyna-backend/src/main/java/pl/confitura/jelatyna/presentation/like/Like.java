package pl.confitura.jelatyna.presentation.like;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.presentation.Presentation;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "presentation_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_like", columnNames = {"presentation_id", "token"})
        })
@Accessors(chain = true)
class Like {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @NotEmpty
    @NotNull
    String token;

    @ManyToOne
    Presentation presentation;
}
