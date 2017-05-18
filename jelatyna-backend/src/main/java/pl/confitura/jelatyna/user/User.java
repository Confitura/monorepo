package pl.confitura.jelatyna.user;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.rest.core.config.Projection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.presentation.Presentation;

@Entity
@Data
@ToString(exclude = "presentations")
@EqualsAndHashCode(exclude = "presentations")
@Accessors(chain = true)
public class User {
    @Id
    private String id;
    private String origin;
    //    @NotBlank
    private String name;
//    @NotNull
    private String email;
//    @NotNull
    @Column(length = 1000)
    private String bio;
    private String username;
    private String twitter;
    private String github;
    private String www;
    private String photo;
    private boolean isAdmin;
    private boolean isVolunteer;
    @OneToMany(mappedBy = "speaker")
    private Set<Presentation> presentations;

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

        Set<Presentation> getPresentations();
    }
}
