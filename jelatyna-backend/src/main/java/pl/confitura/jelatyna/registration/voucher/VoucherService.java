package pl.confitura.jelatyna.registration.voucher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.registration.ParticipapationData;
import pl.confitura.jelatyna.registration.ParticipationRepository;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final ParticipationRepository participationRepository;

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

    public boolean canAssign(String participationDataId, Voucher voucher) {
        if(voucher == null){
            return true;
        }
        ParticipapationData owner = participationRepository.findByVoucher(voucher);
        return owner == null || owner.getId().equals(participationDataId);
    }
}
