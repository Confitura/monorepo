package pl.confitura.jelatyna.agenda.api;

public record AssignAgendaEntryRequest(
        String dayId,
        int timeSlotIndex,
        String roomId,
        String label,
        String presentationId
) {
}
