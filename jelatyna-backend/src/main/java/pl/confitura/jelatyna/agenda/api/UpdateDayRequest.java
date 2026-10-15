package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Request payload for updating a day. Date format should be ISO-8601 local date (e.g. 2026-09-19).
 */
public record UpdateDayRequest(
        @Schema(requiredMode = REQUIRED) String label,
        @Schema(requiredMode = REQUIRED, description = "Date of the day, e.g. 2026-09-19") String date,
        @Schema(requiredMode = REQUIRED) int displayOrder
) {
}
