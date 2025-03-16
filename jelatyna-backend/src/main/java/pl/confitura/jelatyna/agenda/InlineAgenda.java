package pl.confitura.jelatyna.agenda;

import lombok.Data;
import lombok.Value;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.user.PublicUser;

import java.util.Set;

@Value
public class InlineAgenda {
    String id;
    String timeSlotId;
    String timeSlotLabel;
    String roomId;
    String roomLabel;
    String label;
    String presentationId;
    Presentation presentation;
    Set<PublicUser> speakers;

    public InlineAgenda(AgendaEntry target) {
        this.id = target.getId();
        this.timeSlotId = target.getTimeSlot() != null ? target.getTimeSlot().getId() : null;
        this.timeSlotLabel = target.getTimeSlot() != null ? target.getTimeSlot().getLabel() : null;
        this.roomId = target.getRoom() != null ? target.getRoom().getId() : null;
        this.roomLabel = target.getRoom() != null ? target.getRoom().getLabel() : null;
        this.label = target.getLabel();
        this.presentationId = target.getPresentation() != null ? target.getPresentation().getId() : null;
        this.presentation = target.getPresentation();
        this.speakers = target.getSpeakers();
    }

}
