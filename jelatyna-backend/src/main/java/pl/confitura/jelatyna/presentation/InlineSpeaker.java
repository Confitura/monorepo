package pl.confitura.jelatyna.presentation;

import java.util.Set;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "inlineSpeaker", types = { Presentation.class })
interface InlineSpeaker {
    String getId();

    String getTitle();

    String getLanguage();

    String getLevel();

    String getShortDescription();

    String getDescription();

    Set<Tag> getTags();

    Set<SpeakerEntity> getSpeakers();

    String getStatus();

    boolean isWorkshop();

}
