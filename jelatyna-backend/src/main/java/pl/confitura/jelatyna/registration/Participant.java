package pl.confitura.jelatyna.registration;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.user.User;

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
    private Voucher voucher;

    private boolean isAdmin;
    private boolean isVolunteer;
    private boolean isSpeaker;
    private boolean hasAcceptedPresentation;
    private boolean isParticipant;

    public Participant(User user, JelatynaPrincipal registerer) {
        this.id = user.getId();
        if (registerer.isAdmin()) {
            this.name = user.getName();
            this.email = user.getEmail();
        }
        this.isAdmin = user.isAdmin();
        this.isVolunteer = user.isVolunteer();
        this.isSpeaker = user.isSpeaker();
        this.hasAcceptedPresentation = user.hasAcceptedPresentation();
        this.isParticipant = user.isParticipant();

        ParticipationData data = user.getParticipationData();
        if (data != null) {
            this.size = data.getSize();
            this.gender = data.getGender();
            this.arrivalDate = data.getArrivalDate();
            this.registeredBy = data.getRegisteredBy();
            this.surveySendDate = data.getSurveySendDate();

            if (data.getVoucher() != null) {
                this.ticketSendDate = data.getVoucher().getTicketSendDate();
                this.voucher = data.getVoucher();
            }
        }
    }
}
