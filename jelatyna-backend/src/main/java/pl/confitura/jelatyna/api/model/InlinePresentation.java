package pl.confitura.jelatyna.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.Tag;

public record InlinePresentation(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String shortDescription,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String level,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String language,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String[] tags
) {
    public InlinePresentation(Presentation presentation) {
        this(
                presentation.getId(),
                presentation.getTitle(),
                presentation.getShortDescription(),
                presentation.getDescription(),
                presentation.getLevel(),
                presentation.getLanguage(),
                presentation.getTags().stream().map(Tag::getId).toArray(String[]::new)
        );
    }
}
