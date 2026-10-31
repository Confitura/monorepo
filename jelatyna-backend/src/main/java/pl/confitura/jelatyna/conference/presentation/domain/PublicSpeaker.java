package pl.confitura.jelatyna.conference.presentation.domain;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import pl.confitura.jelatyna.user.User;

public record PublicSpeaker(
        String id,
        String name,
        String bio,
        String twitter,
        String github,
        String www,
        String photo,
        List<Presentation> presentations) {

    // Each speaker of these talks, with those of the talks they give.
    public static Set<PublicSpeaker> of(Collection<pl.confitura.jelatyna.conference.presentation.domain.Presentation> talks) {
        return talks.stream().flatMap(talk -> talk.getSpeakers().stream()).distinct()
                .map(speaker -> from(speaker, talks.stream().filter(talk -> talk.getSpeakers().contains(speaker)).toList()))
                .collect(toSet());
    }

    public static PublicSpeaker from(User user, Collection<pl.confitura.jelatyna.conference.presentation.domain.Presentation> talks) {
        return new PublicSpeaker(
                user.getId(),
                user.getName(),
                user.getBio(),
                user.getTwitter(),
                user.getGithub(),
                user.getWww(),
                user.getPhoto(),
                talks.stream().map(Presentation::from).toList()
        );
    }

    record Presentation(String id, String name, Boolean isWorkshop) {
        static Presentation from(pl.confitura.jelatyna.conference.presentation.domain.Presentation presentation) {
            return new Presentation(presentation.getId(), presentation.getTitle(), presentation.isWorkshop());
        }
    }
}
