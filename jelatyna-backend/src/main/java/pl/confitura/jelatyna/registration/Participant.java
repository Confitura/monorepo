package pl.confitura.jelatyna.registration;

import lombok.Data;
import pl.confitura.jelatyna.user.User;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
class Participant implements Serializable {

    private String id;
    private String name;
    private String email;

    private String gender;
    private String size;

    private LocalDateTime arrivalDate;
    private String registeredBy;
    private LocalDateTime ticketSendDate;
    private LocalDateTime surveySendDate;

    public Participant(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        ParticipationData data = user.getParticipationData();
        if (data != null) {
            this.size = data.getSize();
            this.gender = data.getGender();
            arrivalDate = data.getArrivalDate();
            registeredBy = data.getRegisteredBy();
            surveySendDate = data.getSurveySendDate();

            if (data.getVoucher() != null) {
                ticketSendDate = data.getVoucher().getTicketSendDate();
            }
        }
    }
}