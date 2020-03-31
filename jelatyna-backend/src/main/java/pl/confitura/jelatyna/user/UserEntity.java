package pl.confitura.jelatyna.user;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.confitura.jelatyna.agenda.AgendaEntry;
import pl.confitura.jelatyna.agenda.TimeSlot;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.user.dto.FullUserDto;

@Entity
@Table(name = "user")
@Data
@EqualsAndHashCode(callSuper = false)
class UserEntity extends AuditedEntity {
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

    private boolean privacyPolicyAccepted = false;
    private boolean speaker = false;

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

    FullUserDto toDto() {
        return new FullUserDto()
                .setId(this.id)
                .setOrigin(this.origin)
                .setName(this.name)
                .setEmail(this.email)
                .setBio(this.bio)
                .setUsername(this.username)
                .setTwitter(this.twitter)
                .setGithub(this.github)
                .setWww(this.www)
                .setPhoto(this.photo)
                .setAdmin(this.isAdmin)
                .setVolunteer(this.isVolunteer)
                .setSocialId(this.socialId)
                .setPrivacyPolicyAccepted(this.privacyPolicyAccepted)
                .setSpeaker(this.speaker);
    }


    static UserEntity fromDto(FullUserDto dto) {
        return new UserEntity()
                .setId(dto.getId())
                .setOrigin(dto.getOrigin())
                .setName(dto.getName())
                .setEmail(dto.getEmail())
                .setBio(dto.getBio())
                .setUsername(dto.getUsername())
                .setTwitter(dto.getTwitter())
                .setGithub(dto.getGithub())
                .setWww(dto.getWww())
                .setPhoto(dto.getPhoto())
                .setAdmin(dto.isAdmin())
                .setVolunteer(dto.isVolunteer())
                .setSocialId(dto.getSocialId())
                .setPrivacyPolicyAccepted(dto.getPrivacyPolicyAccepted());
    }


}
