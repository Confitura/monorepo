package pl.confitura.jelatyna.user;

import static org.springframework.util.StringUtils.hasText;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.conference.agenda.domain.AgendaEntry;
import pl.confitura.jelatyna.conference.agenda.domain.TimeSlot;
import pl.confitura.jelatyna.platform.persistence.AuditedEntity;

@Entity
@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Table(name = "users")
public class User extends AuditedEntity {
    @Id
    @Column(columnDefinition = "varchar(255)")
    @ToString.Include
    private String id = UUID.randomUUID().toString();
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
    private String photo;
    @ToString.Include
    private boolean isAdmin;
    @ToString.Include
    private boolean isVolunteer;

    @ToString.Include
    private Boolean privacyPolicyAccepted = false;

    // The ticket (registration's ParticipationData) by id; responses show the ticket itself.
    @JsonIgnore
    private String participationDataId;

    public boolean isParticipant() {
        return participationDataId != null;
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

    // Whether the User holds anything worth keeping, so a cancelled merge must not delete it; its talks are asked of
    // Speakers. Sign-in fills in the provider's name and photo, but the privacy policy is accepted only by saving the
    // profile, so it marks a profile the person made.
    boolean hasContent() {
        return Boolean.TRUE.equals(privacyPolicyAccepted) || isAdmin || isVolunteer || isParticipant();
    }

    // Merge (users): keeps this User's own profile values and takes the absorbed User's only where these are empty;
    // roles and privacy consent combine. The caller moves the Logins, the talks (AbsorbedUserTalks) and the ticket (takeTicket).
    void absorb(User absorbed) {
        name = ownOr(name, absorbed.name);
        email = ownOr(email, absorbed.email);
        bio = ownOr(bio, absorbed.bio);
        username = ownOr(username, absorbed.username);
        twitter = ownOr(twitter, absorbed.twitter);
        github = ownOr(github, absorbed.github);
        www = ownOr(www, absorbed.www);
        photo = ownOr(photo, absorbed.photo);
        isAdmin = isAdmin || absorbed.isAdmin;
        isVolunteer = isVolunteer || absorbed.isVolunteer;
        privacyPolicyAccepted = Boolean.TRUE.equals(privacyPolicyAccepted) || Boolean.TRUE.equals(absorbed.privacyPolicyAccepted);
    }

    // Moves the absorbed User's ticket to this one, which keeps its own if it has one; the merge refuses two tickets.
    void takeTicket(User absorbed) {
        if (!isParticipant()) {
            participationDataId = absorbed.participationDataId;
        }
    }

    // What absorbing the other User would make of this one, on an unsaved copy, so this User stays as it is.
    User mergedWith(User absorbed) {
        User merged = new User().setAdmin(isAdmin).setVolunteer(isVolunteer);
        merged.updateFields(this);
        merged.absorb(absorbed);
        return merged;
    }

    private static String ownOr(String own, String absorbed) {
        return hasText(own) ? own : absorbed;
    }

    interface Edit {

    }
}
