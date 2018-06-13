package pl.confitura.jelatyna.user.parsonalagenda;

import lombok.Data;
import pl.confitura.jelatyna.user.User;

@Data
public class Speaker {
    private String name;
    private String bio;
    private String twitter;
    private String github;
    private String www;
    private String photo;

    public Speaker(User user) {
        this.name = user.getName();
        this.bio = user.getBio();
        this.twitter = user.getTwitter();
        this.github = user.getGithub();
        this.www = user.getWww();
        this.photo = user.getPhoto();
    }
}
