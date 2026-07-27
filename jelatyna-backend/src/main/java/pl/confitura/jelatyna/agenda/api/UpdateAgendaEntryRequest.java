package pl.confitura.jelatyna.agenda.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

/**
 * Request payload for updating an agenda entry. It replaces the content of the entry - fields that are
 * not provided (or provided as null) are cleared, which is how a presentation or a room gets unassigned.
 * The time slot of an entry is changed with {@link UpdateAgendaEntrySlotRequest}.
 */
public record UpdateAgendaEntryRequest(
        @Schema(requiredMode = NOT_REQUIRED) Optional<String> label,
        @Schema(requiredMode = NOT_REQUIRED) Optional<String> presentationId,
        @Schema(requiredMode = NOT_REQUIRED) Optional<String> roomId
) {
}
