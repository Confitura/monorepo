package pl.confitura.jelatyna.api.model;

import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.Tag;
import pl.confitura.jelatyna.user.User;

import java.util.List;
import java.util.stream.Collectors;

public record FullPresentation(
        String id,
        String title,
        String shortDescription,
        String description,
        String level,
        String language,
        String[] tags,
        String flatTags,
        Boolean isWorkshop,
        Boolean isFree,
        Double expectedPrice,
        Integer durationInMinutes,
        Integer maxGroupSize,
        String status,
        List<Speaker> speakers,
        String flatSpeakers
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
