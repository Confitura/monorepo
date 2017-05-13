package pl.confitura.jelatyna.user;

import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.presentation.Presentation;

@Entity
@Data
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
    @JsonIgnore
    private Set<Presentation> presentations;

    interface Edit {

    }
}
