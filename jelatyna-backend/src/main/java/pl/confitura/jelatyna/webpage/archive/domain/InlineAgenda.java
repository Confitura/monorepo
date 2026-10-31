package pl.confitura.jelatyna.webpage.archive.domain;

import pl.confitura.jelatyna.conference.agenda.domain.InlineAgendaEntry;
import pl.confitura.jelatyna.conference.agenda.domain.InlineRoom;
import pl.confitura.jelatyna.conference.agenda.domain.InlineTimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record InlineAgenda(
        LocalDate date,
        List<InlineTimeSlot> timeSlots,
        List<InlineRoom> rooms,
        List<InlinePresentationWithSpeakers> presentations,
        List<InlineAgendaEntry> agendaEntries,
        Map<Integer, List<InlineAgendaEntry>> byTimeSlot
) {

}
