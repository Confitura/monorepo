package pl.confitura.jelatyna.voting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
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
