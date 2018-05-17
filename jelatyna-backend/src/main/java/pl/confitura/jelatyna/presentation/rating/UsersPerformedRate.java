package pl.confitura.jelatyna.presentation.rating;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.user.User;

import javax.persistence.*;


@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UsersPerformedRate {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @ManyToOne
    private User user;
    @ManyToOne
    private Presentation presentation;

    public UsersPerformedRate(User user, Presentation presentation) {
        this.user = user;
        this.presentation = presentation;
    }
}
