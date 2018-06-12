package pl.confitura.jelatyna.user;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

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
@ToString(exclude = "presentations")
@EqualsAndHashCode(exclude = "presentations", callSuper = false)
@Accessors(chain = true)
public class User extends AuditedEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(255)")
    private String id;
    private String origin;
    private String name;
    private String email;
    @Column(length = 1000)
    private String bio;
    private String username;
    private String twitter;
    private String github;
    private String www;
    private String photo;
    private boolean isAdmin;
    private boolean isVolunteer;
    @Column(name = "social_id")
    private String socialId;

    private Boolean privacyPolicyAccepted = false;

    @OneToMany(mappedBy = "speaker")
    @JsonIgnore
    private Set<Presentation> presentations;

    @ManyToMany
    private Set<AgendaEntry> personalAgenda = new LinkedHashSet<>();

    @OneToOne
    private ParticipationData participationData;

    public boolean isSpeaker() {
        return presentations != null
                && !presentations.isEmpty();
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

//    @ManyToMany(mappedBy = "cospeakers")
//    @JsonIgnore
//    private Set<Presentation> cospeaking;

    interface Edit {

    }

    @Projection(name = "withPresentations", types = { User.class })
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
