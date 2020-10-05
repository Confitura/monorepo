package pl.confitura.jelatyna.presentation;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Presentation {
    private String id;
    private String title;
    private String shortDescription;
    private String description;
    private String level;
    private String language;
    private Set<Tag> tags = new HashSet<>();
    private Set<SpeakerEntity> speakers = new HashSet<>();
    private String status;
    private boolean workshop = false;

    public static Presentation from(PresentationEntity saved) {
        return new Presentation()
                .setId(saved.getId())
                .setTitle(saved.getTitle())
                .setShortDescription(saved.getShortDescription())
                .setDescription(saved.getDescription())
                .setLevel(saved.getLevel())
                .setLanguage(saved.getLanguage())
                .setTags(saved.getTags())
                .setSpeakers(saved.getSpeakers())
                .setStatus(saved.getStatus())
                .setWorkshop(saved.isWorkshop());
    }

    public PresentationEntity toEntity() {
        return new PresentationEntity()
                .setId(getId())
                .setTitle(getTitle())
                .setShortDescription(getShortDescription())
                .setDescription(getDescription())
                .setLevel(getLevel())
                .setLanguage(getLanguage())
                .setTags(getTags())
                .setSpeakers(getSpeakers())
                .setStatus(getStatus())
                .setWorkshop(isWorkshop());
    }

    public Presentation addSpeaker(SpeakerEntity speaker) {
        speakers.add(speaker);
        return this;
    }
}
