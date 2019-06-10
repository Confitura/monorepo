package pl.confitura.jelatyna.registration;

import lombok.Data;
import pl.confitura.jelatyna.registration.demographic.DemographicData;
import pl.confitura.jelatyna.registration.voucher.Voucher;

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
                .setMealOption(mealOption).setRole(role)
                .setExperience(experience);
    }
}
