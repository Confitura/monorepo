package pl.confitura.jelatyna.user.dto;


import lombok.Data;

@Data
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
    private boolean isSpeaker = false;

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
        return new PublicUserDto()
                .setId(this.getId())
                .setName(this.getName())
                .setBio(this.getBio())
                .setTwitter(this.getTwitter())
                .setGithub(this.getGithub())
                .setWww(this.getWww())
                .setPhoto(this.getPhoto());
    }
}
