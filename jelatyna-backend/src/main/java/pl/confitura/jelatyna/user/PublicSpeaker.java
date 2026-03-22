package pl.confitura.jelatyna.user;

import java.util.List;

public record PublicSpeaker(
        String id,
        String name,
        String bio,
        String twitter,
        String github,
        String www,
        String photo,
        List<Presentation> presentations) {

    public static PublicSpeaker from(User user) {
        return new PublicSpeaker(
                user.getId(),
                user.getName(),
                user.getBio(),
                user.getTwitter(),
                user.getGithub(),
                user.getWww(),
                user.getPhoto(),
                user.getPresentations().stream().map(Presentation::from).toList()
        );
    }

    record Presentation(String id, String name, Boolean isWorkshop) {
        static Presentation from(pl.confitura.jelatyna.presentation.Presentation presentation) {
            return new Presentation(presentation.getId(), presentation.getTitle(), presentation.isWorkshop());
        }
    }
}
