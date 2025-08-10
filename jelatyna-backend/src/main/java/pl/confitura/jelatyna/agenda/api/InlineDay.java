package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;
import pl.confitura.jelatyna.agenda.Day;

public record InlineDay(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String date,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String label,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        int displayOrder
) {
    public static InlineDay from(Day it) {
        return new InlineDay(it.getId(), it.getDate().toString(), it.getLabel(), it.getDisplayOrder());
    }
}
