package pl.confitura.jelatyna.agenda.api;

import pl.confitura.jelatyna.agenda.Day;

public record InlineDay(
        String id,
        String date,
        String label,
        int displayOrder
) {
    public static InlineDay from(Day it) {
        return new InlineDay(it.getId(), it.getDate().toString(), it.getLabel(), it.getDisplayOrder());
    }
}
