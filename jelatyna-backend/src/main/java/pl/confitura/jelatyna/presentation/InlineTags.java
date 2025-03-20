package pl.confitura.jelatyna.presentation;

import java.util.Set;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "inlineTags", types = { Presentation.class })
interface InlineTags {
    String getId();

    String getTitle();

    String getLanguage();

    String getLevel();

    String getShortDescription();

    String getDescription();

    String getStatus();

    Set<Tag> getTags();

}
