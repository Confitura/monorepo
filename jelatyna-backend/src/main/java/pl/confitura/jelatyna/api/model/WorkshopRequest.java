package pl.confitura.jelatyna.api.model;

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
