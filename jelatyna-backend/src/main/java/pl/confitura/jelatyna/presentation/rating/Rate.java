package pl.confitura.jelatyna.presentation.rating;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.presentation.Presentation;

import jakarta.persistence.*;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "presentation_rate")
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "`value`") // `value` is a reserved word in H2; backticks make Hibernate quote it per dialect
    private RateValue value;

    private String comment;

    // A reviewer's own secret: kept out of every response and dump.
    @JsonIgnore
    private String reviewerToken;

    // Not serialized, so a Rate never carries its talk, nor through it the talk's other ratings.
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "presentation_id")
    private Presentation presentation;

    public Rate update(Rate rate) {
        assert reviewerToken.equals(rate.getReviewerToken());
        value = rate.getValue();
        comment = rate.getComment();
        return this;
    }
}
