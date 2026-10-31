package pl.confitura.jelatyna.conference.presentation.domain;

public record PresentationRequest(
        String title,
        String shortDescription,
        String description,
        String level,
        String language,
        String[] tags
) {
}
