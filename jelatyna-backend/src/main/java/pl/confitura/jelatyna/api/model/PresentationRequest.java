package pl.confitura.jelatyna.api.model;

public record PresentationRequest(
        String title,
        String shortDescription,
        String description,
        String level,
        String language,
        String[] tags
) {
}
