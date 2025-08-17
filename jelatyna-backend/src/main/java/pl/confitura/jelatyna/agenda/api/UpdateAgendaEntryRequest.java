package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

/**
 * Request payload for updating an agenda entry. All fields are optional; only provided fields will be updated.
 */
public record UpdateAgendaEntryRequest(
        @Schema(requiredMode = NOT_REQUIRED) Optional<String> label,
        @Schema(requiredMode = NOT_REQUIRED) Optional<String> presentationId,
        @Schema(requiredMode = NOT_REQUIRED) Optional<String> roomId
) {
}
