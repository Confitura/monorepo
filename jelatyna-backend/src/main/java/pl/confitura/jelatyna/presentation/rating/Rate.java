package pl.confitura.jelatyna.presentation.rating;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.presentation.Presentation;

import jakarta.persistence.*;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "presentation_rate")
public class Rate {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @Enumerated(EnumType.STRING)
    private RateValue value;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "presentation_id")
    private Presentation presentation;
}
