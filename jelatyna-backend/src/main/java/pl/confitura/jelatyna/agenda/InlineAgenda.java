package pl.confitura.jelatyna.agenda;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.user.PublicUser;

import java.util.Set;

@Projection(name = "inlineAgenda", types = {AgendaEntry.class})
public interface InlineAgenda {
    String getId();

    @Value("#{target.timeSlot.id}")
    String getTimeSlotId();

    @Value("#{target.timeSlot.label}")
    String getTimeSlotLabel();

    @Value("#{target.room == null ? null : target.room.id}")
    String getRoomId();

    @Value("#{target.label == null ? null : target.label}")
    String getLabel();

    @Value("#{target.presentation == null ? null : target.presentation.id}")
    String getPresentationId();

    Presentation getPresentation();

    @Value("#{target.getSpeaker()}")
    PublicUser getSpeaker();

    @Value("#{target.getCospeakers()}")
    Set<PublicUser> getCospeakers();

}
