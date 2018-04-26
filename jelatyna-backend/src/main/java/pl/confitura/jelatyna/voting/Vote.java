package pl.confitura.jelatyna.voting;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.presentation.Presentation;

@Entity
@Data
@Accessors(chain = true)
public class Vote {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;
    private String token;
    @Column(name = "vote_order")
    private Integer order;
    @OneToOne
    private Presentation presentation;
    @Max(1)
    @Min(-1)
    private Integer rate;
    @JsonIgnore
    private LocalDateTime voteDate;
    @JsonIgnore
    private String client;
}
