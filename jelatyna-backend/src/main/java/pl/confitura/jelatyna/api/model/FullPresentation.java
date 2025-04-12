package pl.confitura.jelatyna.api.model;

import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.Tag;

public record FullPresentation(
        String id,
        String title,
        String shortDescription,
        String description,
        String level,
        String language,
        String[] tags,
        Boolean isWorkshop,
        Boolean isFree,
        Double expectedPrice,
        Integer durationInMinutes,
        Integer maxGroupSize,
        String status
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
                presentation.isWorkshop(),
                presentation.getIsFree(),
                presentation.getExpectedPrice(),
                presentation.getDurationInMinutes(),
                presentation.getMaxGroupSize(),
                presentation.getStatus()
        );
    }
}
