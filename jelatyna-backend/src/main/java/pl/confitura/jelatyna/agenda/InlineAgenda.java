package pl.confitura.jelatyna.agenda;

import org.springframework.beans.factory.annotation.Value;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.user.PublicProfile;

import java.util.Set;

public interface InlineAgenda {
    String getId();

    @Value("#{target.timeSlot.id}")
    String getTimeSlotId();

    @Value("#{target.timeSlot.label}")
    String getTimeSlotLabel();

    @Value("#{target.room == null ? null : target.room.id}")
    String getRoomId();

    @Value("#{target.room == null ? null : target.room.label}")
    String getRoomLabel();

    @Value("#{target.label == null ? null : target.label}")
    String getLabel();

    @Value("#{target.presentation == null ? null : target.presentation.id}")
    String getPresentationId();

    Presentation getPresentation();

    @Value("#{target.getSpeakers()}")
    Set<PublicProfile> getSpeaker();

}
