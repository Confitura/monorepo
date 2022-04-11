package pl.confitura.jelatyna.presentation.rating;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Accessors(chain = true)
public class Rate {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @Enumerated(EnumType.STRING)
    private RateValue value;

    private String comment;
}
