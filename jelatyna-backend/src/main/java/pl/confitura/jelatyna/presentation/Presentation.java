package pl.confitura.jelatyna.presentation;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.confitura.jelatyna.presentation.rating.Rate;
import pl.confitura.jelatyna.user.User;

@Entity
@Data
@ToString(exclude = {"speaker", "ratings"})
@EqualsAndHashCode(exclude = {"speaker", "ratings"})
@Accessors(chain = true)
public class Presentation {

    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REPORTED = "reported";

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;
    @NotBlank
    private String title;
    @NotBlank
    @Column(length = 300)
    private String shortDescription;
    @NotBlank()
    @Column(length = 1000)
    private String description;
    @NotBlank
    private String level;
    @NotBlank
    private String language;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();
    @ManyToOne(optional = false)
    @NotNull
    private User speaker;
    @ManyToMany
    @NotNull
    private Set<User> cospeakers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Rate> ratings = new HashSet<>();

    private String status;

    private boolean workshop = false;

    boolean isOwnedBy(String email) {
        return speaker.getEmail().equalsIgnoreCase(email);
    }

    boolean hasCospeaker(@PathVariable String email) {
        return getCospeakers().stream().anyMatch(it -> it.getEmail().equalsIgnoreCase(email));
    }

    public boolean isAccepted() {
        return STATUS_ACCEPTED.equals(status);
    }

    public void setAccepted(boolean accepted) {
        if (accepted) {
            status = STATUS_ACCEPTED;
        } else {
            status = STATUS_REPORTED;
        }
    }
}
