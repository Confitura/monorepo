package pl.confitura.jelatyna.conference.registration.web;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.user.User;

/**
 * A User as the API shows it: the profile with its ticket. User keeps only the ticket's id, so the web layer adds
 * the ticket here, under the schema name and shape the API has always had.
 */
@Schema(name = "User")
public record UserWithTicket(@JsonUnwrapped User user, ParticipationData participationData) {
}
