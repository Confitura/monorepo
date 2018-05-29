package pl.confitura.jelatyna.registration.voucher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.registration.Participant;
import pl.confitura.jelatyna.registration.ParticipantRepository;
import pl.confitura.jelatyna.user.User;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final ParticipantRepository participantRepository;

    public Voucher generateVoucher(String originalBuyer, String createdBy) {
        return voucherRepository.save(new Voucher()
                .setCreationDate(now()))
                .setOriginalBuyer(originalBuyer)
                .setCreatedBy(createdBy);
    }

    public boolean isValid(Voucher voucher) {
        if (voucher == null || voucher.getId() == null) {
            return false;
        } else {
            return voucherRepository.existsById(voucher.getId());
        }
    }

    public List<Voucher> findUnusedVouchers() {
        return voucherRepository.findUnusedVouchers();
    }

    public Voucher findById(String id){
        return voucherRepository.findById(id);
    }

    public boolean canAssign(String participantId, Voucher voucher) {
        if(voucher == null){
            return true;
        }
        Participant participant = participantRepository.findByVoucher(voucher);
        return participant == null || participant.getId().equals(participantId);
    }
}
