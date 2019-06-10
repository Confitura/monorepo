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

    private boolean isSpeaker;
    private boolean isSponsor;
    private boolean hasAcceptedPresentation;
    private boolean isParticipant;

    public Participant(ParticipationData data, JelatynaPrincipal registerer) {
        this.id = data.getId();
        if (registerer.isAdmin()) {
            this.name = data.getFullName();
            this.email = data.getEmail();
        }
        this.isSpeaker = data.getVoucher().getType() == Voucher.VoucherType.SPEAKER;
        this.isSponsor = data.getVoucher().getType() == Voucher.VoucherType.SPONSOR;
        this.hasAcceptedPresentation = true;
        this.isParticipant = true;

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
