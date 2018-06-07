package pl.confitura.jelatyna.registration;

import lombok.Data;
import pl.confitura.jelatyna.registration.demographic.DemographicData;
import pl.confitura.jelatyna.registration.voucher.Voucher;

@Data
class RegistrationForm {
    private Voucher voucher;
    private String size;
    private String gender;
    private String mealOption;

    private String city;
    private String experience;
    private String role;

    public ParticipationData createParticipant() {
        return new ParticipationData()
                .setVoucher(voucher)
                .setSize(size)
                .setGender(gender)
                .setMealOption(mealOption);
    }

    public DemographicData createDemographicData() {
        return new DemographicData()
                .setCity(city)
                .setRole(role)
                .setExperience(experience);
    }
}
