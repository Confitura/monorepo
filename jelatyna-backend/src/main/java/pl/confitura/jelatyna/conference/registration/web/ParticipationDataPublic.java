package pl.confitura.jelatyna.conference.registration.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationData;

@Data
@NoArgsConstructor
public class ParticipationDataPublic {

    private String id;
    private String lastName;
    private String firstName;
    private String email;

    public ParticipationDataPublic(ParticipationData data) {
        this.id = data.getId();
        this.email = data.getEmail();
        this.firstName = data.getFirstName();
        this.lastName = data.getLastName();
    }
}
