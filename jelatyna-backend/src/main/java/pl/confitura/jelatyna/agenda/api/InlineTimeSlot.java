package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;
import pl.confitura.jelatyna.agenda.TimeSlot;

public record InlineTimeSlot(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Day identifier")
        String dayId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        int displayOrder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String label,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String start,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String end
) {
    public static InlineTimeSlot from(TimeSlot it) {
        return new InlineTimeSlot(it.getId().dayId(), it.getDisplayOrder(), it.getLabel(), it.getStart().toString(), it.getEnd().toString());
    }
}
