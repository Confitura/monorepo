package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

/**
 * Request payload for updating a time slot. All fields are optional; only provided fields will be updated.
 * Time format should be ISO-8601 local time (e.g., HH:mm or HH:mm:ss), as supported by LocalTime.parse.
 */
public record UpdateTimeSlotRequest(
        @Schema(requiredMode = NOT_REQUIRED, description = "Start time, e.g. 09:00") Optional<String> start,
        @Schema(requiredMode = NOT_REQUIRED, description = "End time, e.g. 10:00") Optional<String> end,
        @Schema(requiredMode = NOT_REQUIRED) Optional<Boolean> forAllRooms
) {
}
