package pl.confitura.jelatyna.agenda.api;

import pl.confitura.jelatyna.agenda.AgendaEntry;

public record InlineAgendaEntry(
        String id,
        String dayId,
        int timeSlotIndex,
        String roomId,
        String label,
        String presentationId
) {
    public static InlineAgendaEntry from(AgendaEntry it) {
        return new InlineAgendaEntry(it.getId(), it.getDay().getId(), it.getTimeSlotOrder(), it.getRoomId(), it.getLabel(), it.getPresentationId());
    }
}
