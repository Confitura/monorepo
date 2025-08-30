package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;
import pl.confitura.jelatyna.agenda.AgendaEntry;

public record InlineAgendaEntry(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String dayId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        int timeSlotIndex,
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String roomId,
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String label,
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String presentationId,
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        int timeSlotSpan
) {
    public static InlineAgendaEntry from(AgendaEntry it) {
        return new InlineAgendaEntry(
                it.getId(),
                it.getTimeSlot().getId().dayId(),
                it.getTimeSlotOrder(),
                it.getRoomId(),
                it.getLabel(),
                it.getPresentationId(),
                it.getTimeSlot().getTimeSlotSpan());
    }
}
