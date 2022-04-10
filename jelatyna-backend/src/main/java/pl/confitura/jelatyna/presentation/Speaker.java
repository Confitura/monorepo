package pl.confitura.jelatyna.presentation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.user.dto.PublicUser;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@ToString(exclude = "presentations")
@EqualsAndHashCode(exclude = "presentations", callSuper = false)
@Accessors(chain = true)
@Table(name = "user")
public class Speaker {
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

    public PublicUser toPublicUser() {
        Speaker user = this;
        PublicUser that = new PublicUser();
        that.setId(user.getId());
        that.setName(user.getName());
        that.setBio(user.getBio());
        that.setTwitter(user.getTwitter());
        that.setGithub(user.getGithub());
        that.setWww(user.getWww());
        that.setPhoto(user.getPhoto());
        return that;
    }

    public static Speaker fromUser(pl.confitura.jelatyna.user.dto.User dto) {
        Speaker user = new Speaker();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setBio(dto.getBio());
        user.setTwitter(dto.getTwitter());
        user.setGithub(dto.getGithub());
        user.setWww(dto.getWww());
        user.setPhoto(dto.getPhoto());
        return user;
    }

}
