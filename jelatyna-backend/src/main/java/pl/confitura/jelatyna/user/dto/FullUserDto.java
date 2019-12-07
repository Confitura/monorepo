package pl.confitura.jelatyna.user.dto;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FullUserDto {
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

    public void updateFields(FullUserDto user) {
        name = user.name;
        email = user.email;
        bio = user.bio;
        username = user.username;
        twitter = user.twitter;
        github = user.github;
        www = user.www;
        photo = user.photo;
        privacyPolicyAccepted = user.privacyPolicyAccepted;
    }

    public PublicUserDto toPublicUser() {
        PublicUserDto that = new PublicUserDto();
        that.setId(this.getId());
        that.setName(this.getName());
        that.setBio(this.getBio());
        that.setTwitter(this.getTwitter());
        that.setGithub(this.getGithub());
        that.setWww(this.getWww());
        that.setPhoto(this.getPhoto());
        return that;
    }
}
