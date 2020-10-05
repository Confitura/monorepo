package pl.confitura.jelatyna.agenda;

import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.user.dto.PublicUserDto;

import java.util.Set;

public class InlineAgenda {
    private final AgendaEntry agendaEntry;
    private final Presentation presentation;

    public InlineAgenda(AgendaEntry agendaEntry, Presentation presentation) {
        this.agendaEntry = agendaEntry;
        this.presentation = presentation;
    }

    public String getId() {
        return agendaEntry.getId();
    }

    public String getTimeSlotId() {
        if(agendaEntry.getTimeSlot() == null ){
            return null;
        }
        return agendaEntry.getTimeSlot().getId();
    }

    public String getTimeSlotLabel() {
        if(agendaEntry.getTimeSlot() == null ){
            return null;
        }
        return agendaEntry.getTimeSlot().getLabel();
    }

    public String getRoomId() {
        if(agendaEntry.getRoom() == null ){
            return null;
        }
        return agendaEntry.getRoom().getId();
    }

    public String getRoomLabel() {
        if(agendaEntry.getRoom() == null ){
            return null;
        }
        return agendaEntry.getRoom().getLabel();
    }

    public String getLabel() {
        return agendaEntry.getLabel();
    }

    public String getPresentationId() {
        return agendaEntry.getPresentationId();
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public Set<PublicUserDto> getSpeaker() {
        return agendaEntry.getSpeakers();
    }

}
