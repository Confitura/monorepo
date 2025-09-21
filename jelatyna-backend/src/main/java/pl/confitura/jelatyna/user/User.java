package pl.confitura.jelatyna.user;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.agenda.AgendaEntry;
import pl.confitura.jelatyna.agenda.TimeSlot;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.registration.ParticipationData;

@Entity
@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(exclude = "presentations", callSuper = false)
@Accessors(chain = true)
@Table(name = "users")
public class User extends AuditedEntity {
    @Id
    @Column(columnDefinition = "varchar(255)")
    @ToString.Include
    private String id = UUID.randomUUID().toString();
    @ToString.Include
    @Column(columnDefinition = "text")
    private String origin;
    @Column(columnDefinition = "text")
    private String name;
    @Column(columnDefinition = "text")
    private String email;
    @Column(columnDefinition = "text")
    private String bio;
    @Column(columnDefinition = "text")
    private String username;
    @Column(columnDefinition = "text")
    private String twitter;
    @Column(columnDefinition = "text")
    private String github;
    @Column(columnDefinition = "text")
    private String www;
    @Column(columnDefinition = "text")
    @ToString.Include
    private String photo;
    @ToString.Include
    private boolean isAdmin;
    @ToString.Include
    private boolean isVolunteer;
    @Column(columnDefinition = "text", name = "social_id")
    private String socialId;

    @ToString.Include
    private Boolean privacyPolicyAccepted = false;

    @ManyToMany(mappedBy = "speakers")
    @JsonIgnore
    private Set<Presentation> presentations;

    @OneToOne(fetch = FetchType.LAZY)
    private ParticipationData participationData = null;

    public boolean isSpeaker() {
        return presentations != null
               && !presentations.isEmpty();
    }

    public boolean isParticipant() {
        return participationData != null;
    }


    void updateFields(User user) {
        name = user.name;
        email = user.email;
        bio = user.bio;
        username = user.username;
        twitter = user.twitter;
        github = user.github;
        www = user.www;
        photo = user.photo;
        privacyPolicyAccepted = user.privacyPolicyAccepted;
    }

    public boolean hasAcceptedPresentation() {
        return presentations != null && presentations.stream().anyMatch(Presentation::isAccepted);
    }

    public boolean hasArrived() {
        return isParticipant() && getParticipationData().getArrivalDate() != null;
    }

    interface Edit {

    }

    interface WithPresentations {
        String getId();

        String getOrigin();

        String getName();

        String getEmail();

        String getBio();

        String getTwitter();

        String getGithub();

        String getWww();

        String getPhoto();

        boolean isAdmin();

        @Value("#{target.isSpeaker()}")
        boolean isSpeaker();

        Set<Presentation> getPresentations();
    }
}
