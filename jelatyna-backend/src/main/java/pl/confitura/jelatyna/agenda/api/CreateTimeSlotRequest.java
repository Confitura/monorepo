package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Request payload for creating a time slot in a day.
 * Time format should be ISO-8601 local time (e.g., HH:mm or HH:mm:ss), as supported by LocalTime.parse.
 */
public record CreateTimeSlotRequest(
        @Schema(requiredMode = REQUIRED, description = "Start time, e.g. 09:00") String start,
        @Schema(requiredMode = REQUIRED, description = "End time, e.g. 10:00") String end,
        @Schema(requiredMode = NOT_REQUIRED) Optional<Boolean> forAllRooms
) {
}
