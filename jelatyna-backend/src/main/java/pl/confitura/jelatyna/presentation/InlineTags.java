package pl.confitura.jelatyna.presentation;

import java.util.Set;


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
