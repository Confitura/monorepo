package pl.confitura.jelatyna.registration.voucher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.mail.MailSender;
import pl.confitura.jelatyna.mail.MessageInfo;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.ParticipationRepository;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final ParticipationRepository participationRepository;
    private final MailSender sender;

    public Voucher generateVoucher(String originalBuyer) {
        return generateVoucher(originalBuyer, Voucher.VoucherType.PARTICIPANT, null);
    }

    public Voucher generateVoucher(String originalBuyer, Voucher.VoucherType type, String comment) {
        return voucherRepository.save(new Voucher()
                .setOriginalBuyer(originalBuyer)
                .setComment(comment)
                .setType(type));
    }

    public Voucher generateVoucherFromAllegro(String originalBuyer,
                                              String comment,
                                              String buyerLogin,
                                              String auctionId,
                                              String auctionName) {
        return voucherRepository.save(new Voucher()
                .setOriginalBuyer(originalBuyer)
                .setComment(comment)
                .setType(Voucher.VoucherType.PARTICIPANT)
                .setAllegro(new Voucher.AllegroContext(auctionId, auctionName, buyerLogin))
        );
    }

    public boolean isValid(Voucher voucher) {
        if (voucher == null || voucher.getId() == null) {
            return false;
        } else {
            return voucherRepository.existsById(voucher.getId());
        }
    }

    public boolean isInvalid(String voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId);
        if (voucher == null || voucher.getId() == null) {
            return true;
        } else {
            return !voucherRepository.existsById(voucher.getId());
        }
    }

    public List<Voucher> findUnusedVouchers() {
        return voucherRepository.findUnusedVouchers();
    }

    public Voucher findById(String id) {
        return voucherRepository.findById(id);
    }

    public boolean canAssign(String participationDataId, Voucher voucher) {
        if (voucher == null) {
            return true;
        }
        ParticipationData owner = participationRepository.findByVoucher(voucher);
        return owner == null || owner.getId().equals(participationDataId);
    }

    public boolean isUsed(String voucher) {
        return participationRepository.findByVoucherId(voucher) != null;
    }

    boolean canUseVoucher(String voucherId) {
        return getParticipationDataFor(voucherId) == null;
    }

    ParticipationData getParticipationDataFor(String voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId);
        return participationRepository.findByVoucher(voucher);
    }

    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }

    public void sendVouchers() {
        voucherRepository.findNotSent().forEach(this::sendVoucher);
    }

    public void sendVoucher(Voucher voucher) {
        try {
            sender.send("pre-registration", new MessageInfo().setEmail(voucher.getOriginalBuyer()).setToken(voucher.getId()));
            voucher.setTicketSendDate(now());
            voucherRepository.save(voucher);
        } catch (Exception ex) {
            log.error("Error on sending email", ex);
        }
    }

    void resend(String voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId);
        sendVoucher(voucher);
    }
}
