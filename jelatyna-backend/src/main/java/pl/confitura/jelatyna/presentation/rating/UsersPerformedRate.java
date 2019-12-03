package pl.confitura.jelatyna.presentation.rating;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.user.dto.User;

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

    private String userId;
    private String presentationId;

    public UsersPerformedRate(User user, String presentationId) {
        this.userId = user.getId();
        this.presentationId = presentationId;
    }
}
