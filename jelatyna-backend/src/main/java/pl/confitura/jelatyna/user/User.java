package pl.confitura.jelatyna.user;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

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
import pl.confitura.jelatyna.presentation.Speaker;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.user.dto.PublicUser;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
class User extends AuditedEntity {
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
    private Boolean speaker = false;

    @ManyToMany
    private Set<AgendaEntry> personalAgenda = new LinkedHashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    private ParticipationData participationData = null;

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


    public boolean hasArrived() {
        return isParticipant() && getParticipationData().getArrivalDate() != null;
    }

    pl.confitura.jelatyna.user.dto.User toDto() {
        pl.confitura.jelatyna.user.dto.User dto = new pl.confitura.jelatyna.user.dto.User();
        dto.setId(this.id);
        dto.setOrigin(this.origin);
        dto.setName(this.name);
        dto.setEmail(this.email);
        dto.setBio(this.bio);
        dto.setUsername(this.username);
        dto.setTwitter(this.twitter);
        dto.setGithub(this.github);
        dto.setWww(this.www);
        dto.setPhoto(this.photo);
        dto.setAdmin(this.isAdmin);
        dto.setVolunteer(this.isVolunteer);
        dto.setSocialId(this.socialId);
        dto.setPrivacyPolicyAccepted(this.privacyPolicyAccepted);
        dto.setSpeaker(this.speaker);
        return dto;
    }


    static User fromDto(pl.confitura.jelatyna.user.dto.User dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setOrigin(dto.getOrigin());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setBio(dto.getBio());
        user.setUsername(dto.getUsername());
        user.setTwitter(dto.getTwitter());
        user.setGithub(dto.getGithub());
        user.setWww(dto.getWww());
        user.setPhoto(dto.getPhoto());
        user.setAdmin(dto.isAdmin());
        user.setVolunteer(dto.isVolunteer());
        user.setSocialId(dto.getSocialId());
        user.setPrivacyPolicyAccepted(dto.getPrivacyPolicyAccepted());
        return user;
    }


}
