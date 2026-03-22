package pl.confitura.jelatyna.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.Tag;
import pl.confitura.jelatyna.user.User;

import java.util.List;
import java.util.stream.Collectors;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record FullPresentation(
        @Schema(requiredMode = REQUIRED) String id,
        @Schema(requiredMode = REQUIRED) String title,
        @Schema(requiredMode = REQUIRED) String shortDescription,
        @Schema(requiredMode = REQUIRED) String description,
        @Schema(requiredMode = REQUIRED) String level,
        @Schema(requiredMode = REQUIRED) String language,
        @Schema(requiredMode = REQUIRED) String[] tags,
        @Schema(requiredMode = REQUIRED) String flatTags,
        @Schema(requiredMode = REQUIRED) Boolean isWorkshop,
        @Schema(requiredMode = REQUIRED) Boolean isFree,
        @Schema(requiredMode = REQUIRED) Double expectedPrice,
        @Schema(requiredMode = REQUIRED) Integer durationInMinutes,
        @Schema(requiredMode = REQUIRED) Integer maxGroupSize,
        @Schema(requiredMode = REQUIRED) String status,
        @Schema(requiredMode = REQUIRED) List<Speaker> speakers,
        @Schema(requiredMode = REQUIRED) String flatSpeakers
) {
    public FullPresentation(Presentation presentation) {
        this(
                presentation.getId(),
                presentation.getTitle(),
                presentation.getShortDescription(),
                presentation.getDescription(),
                presentation.getLevel(),
                presentation.getLanguage(),
                presentation.getTags().stream().map(Tag::getId).toArray(String[]::new),
                presentation.getTags().stream().map(Tag::getId).collect(Collectors.joining(", ")),
                presentation.isWorkshop(),
                presentation.getIsFree(),
                presentation.getExpectedPrice(),
                presentation.getDurationInMinutes(),
                presentation.getMaxGroupSize(),
                presentation.getStatus(),
                presentation.getSpeakers().stream().map(Speaker::new).toList(),
                presentation.getSpeakers().stream().map(User::getName).collect(Collectors.joining(", "))
        );
    }

    record Speaker(String name, String photo) {

        public Speaker(User speaker) {
            this(speaker.getName(), speaker.getPhoto());
        }
    }
}
