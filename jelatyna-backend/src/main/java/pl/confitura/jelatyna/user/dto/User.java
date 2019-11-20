package pl.confitura.jelatyna.user.dto;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private String id;
    private String origin;
    private String name;
    private String email;
    private String bio;
    private String username;
    private String twitter;
    private String github;
    private String www;
    private String photo;
    private boolean isAdmin;
    private boolean isVolunteer;
    private String socialId;
    private Boolean privacyPolicyAccepted = false;
    private Boolean speaker = false;
}
