package pl.confitura.jelatyna.presentation;

import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.presentation.rating.Rate;
import pl.confitura.jelatyna.user.PublicUser;
import pl.confitura.jelatyna.user.User;

@Entity
@Data
@ToString(exclude = { "speakers", "ratings" })
@EqualsAndHashCode(exclude = { "speakers", "ratings" })
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

    @ManyToMany
    @NotNull
    private Set<User> speakers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Rate> ratings = new HashSet<>();

    private String status;

    private boolean workshop = false;

    boolean isOwnedBy(String email) {
        return speakers.stream().anyMatch(it -> it.getEmail().equalsIgnoreCase(email));
    }

    boolean hasCospeaker(String email) {
        return speakers.stream().anyMatch(it -> it.getEmail().equalsIgnoreCase(email));
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

    public boolean isNew() {
        return id == null;
    }

    public Presentation setSpeaker(User speaker) {
        speakers.add(speaker);
        return this;
    }

    public Set<PublicUser> getPublicSpeakers() {
        if (getSpeakers().isEmpty()) {
            return Collections.emptySet();
        } else {
            return getSpeakers().stream()
                    .map(PublicUser::new)
                    .collect(toSet());
        }
    }
}
