package pl.confitura.jelatyna.conference.registration.web;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationData;
import pl.confitura.jelatyna.identity.user.domain.User;

/**
 * A User as the API shows it: the profile with its ticket and whether it speaks. User keeps only the ticket's id and
 * doesn't know its talks, so the web layer adds both here, under the schema name and shape the API has always had.
 */
@Schema(name = "User")
public record UserWithTicket(@JsonUnwrapped User user, ParticipationData participationData, boolean speaker) {
}
