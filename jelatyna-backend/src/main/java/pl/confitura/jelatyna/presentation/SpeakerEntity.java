package pl.confitura.jelatyna.presentation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.user.dto.FullUserDto;
import pl.confitura.jelatyna.user.dto.PublicUserDto;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@ToString(exclude = "presentations")
@EqualsAndHashCode(exclude = "presentations", callSuper = false)
@Table(name = "user")
public class SpeakerEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(255)")
    private String id;

    @ManyToMany(mappedBy = "speakers")
    @JsonIgnore
    private Set<Presentation> presentations;


    @Column(updatable = false, insertable = false)
    private String name;
    @Column(updatable = false, insertable = false)
    private String email;
    @Column(updatable = false, insertable = false)
    private String bio;
    @Column(updatable = false, insertable = false)
    private String twitter;
    @Column(updatable = false, insertable = false)
    private String github;
    @Column(updatable = false, insertable = false)
    private String www;
    @Column(updatable = false, insertable = false)
    private String photo;


    public boolean isSpeaker() {
        return presentations != null
                && !presentations.isEmpty();
    }

    public PublicUserDto toPublicUser() {
        return new PublicUserDto()
                .setId(getId())
                .setName(getName())
                .setBio(getBio())
                .setTwitter(getTwitter())
                .setGithub(getGithub())
                .setWww(getWww())
                .setPhoto(getPhoto());
    }

    public static SpeakerEntity fromUser(FullUserDto dto) {
        return new SpeakerEntity()
                .setId(dto.getId())
                .setName(dto.getName())
                .setEmail(dto.getEmail())
                .setBio(dto.getBio())
                .setTwitter(dto.getTwitter())
                .setGithub(dto.getGithub())
                .setWww(dto.getWww())
                .setPhoto(dto.getPhoto());
    }

}
