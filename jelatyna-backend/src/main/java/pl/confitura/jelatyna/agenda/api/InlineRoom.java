package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;
import pl.confitura.jelatyna.agenda.Room;

public record InlineRoom(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String label,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        int displayOrder
) {
    public static InlineRoom from(Room it) {
        return new InlineRoom(it.getId(), it.getLabel(), it.getDisplayOrder());
    }
}
