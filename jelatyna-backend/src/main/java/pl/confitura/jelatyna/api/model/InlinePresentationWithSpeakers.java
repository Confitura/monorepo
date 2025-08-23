package pl.confitura.jelatyna.api.model;

import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.Tag;

import java.util.List;

public record InlinePresentationWithSpeakers(
        String id,
        String title,
        String shortDescription,
        String description,
        String level,
        String language,
        Boolean workshop,
        List<Tag> tags,
        List<Speaker> speakers
) {
    public InlinePresentationWithSpeakers(Presentation presentation) {
        this(
                presentation.getId(),
                presentation.getTitle(),
                presentation.getShortDescription(),
                presentation.getDescription(),
                presentation.getLevel(),
                presentation.getLanguage(),
                presentation.isWorkshop(),
                presentation.getTags().stream().map(Tag::from).toList(),
                presentation.getSpeakers().stream().map(Speaker::from).toList()
        );
    }

    public record Tag(String id, String name) {
        static Tag from(pl.confitura.jelatyna.presentation.Tag tag) {
            return new Tag(tag.getId(), tag.getName());
        }
    }

    public record Speaker(
            String id,
            String name,
            String photoUrl) {
        static Speaker from(pl.confitura.jelatyna.user.User speaker) {
            return new Speaker(
                    speaker.getId(),
                    speaker.getName(),
                    speaker.getPhoto());
        }
    }
}
