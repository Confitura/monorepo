package pl.confitura.jelatyna.registration;

import lombok.Data;
import lombok.NoArgsConstructor;

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
