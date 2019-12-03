package pl.confitura.jelatyna.presentation.like;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "presentation_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_like", columnNames = {"presentationId", "token"})
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
    private String token;

    private String presentationId;
}
