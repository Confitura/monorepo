package pl.confitura.jelatyna.api.model;

import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.Tag;

public record InlinePresentation(
        String id,
        String title,
        String shortDescription,
        String description,
        String level,
        String language,
        String[] tags,
        String status
) {
    public InlinePresentation(Presentation presentation) {
        this(
                presentation.getId(),
                presentation.getTitle(),
                presentation.getShortDescription(),
                presentation.getDescription(),
                presentation.getLevel(),
                presentation.getLanguage(),
                presentation.getTags().stream().map(Tag::getId).toArray(String[]::new),
                presentation.getStatus()
        );
    }
}
