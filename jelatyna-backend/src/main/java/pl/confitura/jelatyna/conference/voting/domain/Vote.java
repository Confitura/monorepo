package pl.confitura.jelatyna.conference.voting.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;

@Entity
@Data
@Accessors(chain = true)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "varchar(100)")
    private String id;
    private String token;
    @Column(name = "vote_order")
    private Integer order;
    @ManyToOne
    private Presentation presentation;
    @Max(1)
    @Min(-1)
    private Integer rate;
    @JsonIgnore
    private LocalDateTime voteDate;
    @JsonIgnore
    private String client;
}
