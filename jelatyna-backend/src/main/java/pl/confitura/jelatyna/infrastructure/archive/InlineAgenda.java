package pl.confitura.jelatyna.infrastructure.archive;

import pl.confitura.jelatyna.agenda.api.InlineAgendaEntry;
import pl.confitura.jelatyna.agenda.api.InlineRoom;
import pl.confitura.jelatyna.agenda.api.InlineTimeSlot;
import pl.confitura.jelatyna.api.model.InlinePresentationWithSpeakers;

import java.util.List;

public record InlineAgenda(
        List<InlineTimeSlot> timeSlots,
        List<InlineRoom> rooms,
        List<InlinePresentationWithSpeakers> presentations,
        List<InlineAgendaEntry> agendaEntries
) {
}
