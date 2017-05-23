package pl.confitura.jelatyna.presentation;

import java.util.Set;

import org.springframework.data.rest.core.config.Projection;

import pl.confitura.jelatyna.user.User;

@Projection(name = "inlineSpeaker", types = { Presentation.class })
interface InlineSpeaker {
    String getId();

    String getTitle();

    String getLanguage();

    String getLevel();

    String getShortDescription();

    String getDescription();

    Set<Tag> getTags();

    User getSpeaker();

}
