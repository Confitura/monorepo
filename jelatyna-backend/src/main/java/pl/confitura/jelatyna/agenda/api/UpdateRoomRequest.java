package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Request payload for updating a room. All fields are optional; only provided fields will be updated.
 */
public record UpdateRoomRequest(
        @Schema(requiredMode = REQUIRED) String label
) {
}
