package pl.confitura.jelatyna.agenda.api;

import pl.confitura.jelatyna.agenda.TimeSlot;

public record InlineTimeSlot(
        String dayId,
        int displayOrder,
        String label,
        String start,
        String end
) {
    public static InlineTimeSlot from(TimeSlot it) {
        return new InlineTimeSlot(it.getId().dayId(), it.getDisplayOrder(), it.getLabel(), it.getStart().toString(), it.getEnd().toString());
    }
}
