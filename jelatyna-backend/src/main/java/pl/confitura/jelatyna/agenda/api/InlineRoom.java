package pl.confitura.jelatyna.agenda.api;

import pl.confitura.jelatyna.agenda.Room;

public record InlineRoom(
        String id,
        String label,
        int displayOrder
) {
    public static InlineRoom from(Room it) {
        return new InlineRoom(it.getId(), it.getLabel(), it.getDisplayOrder());
    }
}
