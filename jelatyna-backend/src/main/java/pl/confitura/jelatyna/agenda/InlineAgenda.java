package pl.confitura.jelatyna.agenda;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.user.User;

import java.util.Set;

@Projection(name = "inlineAgenda", types = {AgendaEntry.class})
interface InlineAgenda {
    String getId();

    @Value("#{target.timeSlot.id}")
    String getTimeSlotId();

    @Value("#{target.room == null ? null : target.room.id}")
    String getRoomId();

    @Value("#{target.label == null ? null : target.label}")
    String getLabel();

    @Value("#{target.presentation == null ? null : target.presentation.id}")
    String getPresentationId();

    Presentation getPresentation();

    @Value("#{target.presentation == null ? null : target.presentation.speaker}")
    User getSpeaker();

    @Value("#{target.presentation == null ? null : target.presentation.cospeakers}")
    Set<User> getCospeakers();

}
