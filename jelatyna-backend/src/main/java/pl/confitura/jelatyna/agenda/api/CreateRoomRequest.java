package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Request payload for creating a room in a day. When display order is not given the room is added as the last one.
 */
public record CreateRoomRequest(
        @Schema(requiredMode = REQUIRED) String label,
        @Schema(requiredMode = NOT_REQUIRED) Optional<Integer> displayOrder
) {
}
