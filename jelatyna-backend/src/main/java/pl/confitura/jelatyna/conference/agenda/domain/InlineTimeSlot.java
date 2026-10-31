package pl.confitura.jelatyna.conference.agenda.domain;

import io.swagger.v3.oas.annotations.media.Schema;

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
        String end,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean forAllRooms
) {
    public static InlineTimeSlot from(TimeSlot it) {
        return new InlineTimeSlot(it.getId().dayId(), it.getDisplayOrder(), it.getLabel(), it.getStart().toString(), it.getEnd().toString(), it.isForAllRooms());
    }
}
