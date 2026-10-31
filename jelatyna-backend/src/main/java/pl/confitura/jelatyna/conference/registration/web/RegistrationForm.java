package pl.confitura.jelatyna.conference.registration.web;

import lombok.Data;
import pl.confitura.jelatyna.conference.registration.domain.DemographicData;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationData;
import pl.confitura.jelatyna.conference.registration.domain.Voucher;

import java.util.Set;

@Data
class RegistrationForm {
    private Voucher voucher;

    private String lastName;
    private String firstName;
    private String email;
    private boolean privacyPolicyAccepted;

    private String size;
    private String gender;
    private String mealOption;

    private String city;
    private String experience;
    private String role;
    private Set<String> technologies;

    public ParticipationData createParticipant() {
        return new ParticipationData()
                .setVoucher(voucher)
                .setLastName(lastName)
                .setFirstName(firstName)
                .setEmail(email)
                .setPrivacyPolicyAccepted(privacyPolicyAccepted)
                .setSize(size)
                .setGender(gender);
    }

    public DemographicData createDemographicData() {
        return new DemographicData()
                .setCity(city)
                .setMealOption(mealOption)
                .setRole(role)
                .setExperience(experience)
                .setTechnologies(technologies);
    }
}
