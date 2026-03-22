package pl.confitura.jelatyna.user;

public record PublicProfile(
        String id,
        String name,
        String bio,
        String twitter,
        String github,
        String www,
        String photo) {

    public static PublicProfile from(User user) {
        return new PublicProfile(
                user.getId(),
                user.getName(),
                user.getBio(),
                user.getTwitter(),
                user.getGithub(),
                user.getWww(),
                user.getPhoto()
        );
    }
}
