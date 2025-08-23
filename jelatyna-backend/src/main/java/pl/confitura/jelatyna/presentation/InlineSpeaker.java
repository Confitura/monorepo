package pl.confitura.jelatyna.presentation;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import pl.confitura.jelatyna.user.PublicProfile;

interface InlineSpeaker {
    String getId();

    String getTitle();

    String getLanguage();

    String getLevel();

    String getShortDescription();

    String getDescription();

    Set<Tag> getTags();

    @Value("#{target.getPublicSpeakers()}")
    Set<PublicProfile> getSpeakers();

    String getStatus();

    boolean isWorkshop();

}
