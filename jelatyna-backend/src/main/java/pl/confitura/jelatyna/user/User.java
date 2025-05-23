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
    private String origin;
    private String name;
    private String email;
    @Column(length = 1000)
    private String bio;
    private String username;
    private String twitter;
    private String github;
    private String www;
    @ToString.Include
    private String photo;
    @ToString.Include
    private boolean isAdmin;
    @ToString.Include
    private boolean isVolunteer;
    @Column(name = "social_id")
    private String socialId;

    @ToString.Include
    private Boolean privacyPolicyAccepted = false;

    @ManyToMany(mappedBy = "speakers")
    @JsonIgnore
    private Set<Presentation> presentations;

    @ManyToMany
    private Set<AgendaEntry> personalAgenda = new LinkedHashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    private ParticipationData participationData = null;

    public boolean isSpeaker() {
        return presentations != null
                && !presentations.isEmpty();
    }

    public boolean isParticipant() {
        return participationData != null;
    }

    public void addToPersonalAgenda(AgendaEntry agendaEntry) {
        personalAgenda.add(agendaEntry);
    }

    public boolean personalAgendaContainsTimeSlot(TimeSlot timeSlot) {
        return personalAgenda.stream().anyMatch(it -> it.getTimeSlot().equals(timeSlot));
    }

    public Optional<AgendaEntry> getFromPersonalAgendaWithTimeSlot(TimeSlot timeSlot) {
        return personalAgenda.stream()
                .filter(it -> it.getTimeSlot().equals(timeSlot))
                .findAny();
    }

    public void removeFromPersonalAgenda(AgendaEntry entry) {
        personalAgenda.remove(entry);
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
        return presentations.stream().anyMatch(Presentation::isAccepted);
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
