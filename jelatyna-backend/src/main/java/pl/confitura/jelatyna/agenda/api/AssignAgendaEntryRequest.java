package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AssignAgendaEntryRequest(
        @Schema(requiredMode = REQUIRED) String dayId,
        @Schema(requiredMode = REQUIRED) int timeSlotIndex,
        @Schema(requiredMode = NOT_REQUIRED) Optional<String> roomId,
        @Schema(requiredMode = REQUIRED) String label,
        @Schema(requiredMode = REQUIRED) String presentationId
) {
}
