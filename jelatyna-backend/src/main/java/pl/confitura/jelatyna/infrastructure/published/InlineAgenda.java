package pl.confitura.jelatyna.infrastructure.published;

import pl.confitura.jelatyna.agenda.api.InlineAgendaEntry;
import pl.confitura.jelatyna.agenda.api.InlineRoom;
import pl.confitura.jelatyna.agenda.api.InlineTimeSlot;
import pl.confitura.jelatyna.api.model.InlinePresentationWithSpeakers;

import java.util.List;
import java.util.Map;

public record InlineAgenda(
        List<InlineTimeSlot> timeSlots,
        List<InlineRoom> rooms,
        List<InlinePresentationWithSpeakers> presentations,
        List<InlineAgendaEntry> agendaEntries,
        Map<Integer, List<InlineAgendaEntry>> byTimeSlot
) {

}
