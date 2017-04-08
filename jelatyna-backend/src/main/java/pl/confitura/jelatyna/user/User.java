package pl.confitura.jelatyna.user;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
//    @NotNull
    private String name;
//    @NotNull
    private String email;
//    @NotNull
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
}
