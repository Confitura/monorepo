package pl.confitura.jelatyna.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PublicUser {
    private String name;
    private String bio;
    private String twitter;
    private String github;
    private String www;
    private String photo;

    public PublicUser(User user) {
        this.name = user.getName();
        this.bio = user.getBio();
        this.twitter = user.getTwitter();
        this.github = user.getGithub();
        this.www = user.getWww();
        this.photo = user.getPhoto();
    }
}
