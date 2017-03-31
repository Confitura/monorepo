package pl.confitura.jelatyna.user;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class User {
    @Id
    private String id;
    private String origin;
    private String name;
    private String email;
    private String username;
    private String bio;
    private String twitter;
    private String github;
    private String www;
    private String photo;
    private boolean isAdmin;
    private boolean isVolunteer;
}
