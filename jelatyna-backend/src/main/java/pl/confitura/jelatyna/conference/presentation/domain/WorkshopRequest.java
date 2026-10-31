package pl.confitura.jelatyna.conference.presentation.domain;

public record WorkshopRequest(
        String title,
        String shortDescription,
        String description,
        String level,
        String language,
        String[] tags,
        Boolean isFree,
        Double expectedPrice,
        Integer durationInMinutes,
        Integer maxGroupSize
) {
}
