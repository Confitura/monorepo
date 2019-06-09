package pl.confitura.jelatyna.registration.voucher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.ParticipationRepository;
import pl.confitura.jelatyna.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;

    public Voucher generateVoucher(String originalBuyer) {
        return generateVoucher(originalBuyer, Voucher.VoucherType.PARTICIPANT, null);
    }

    Voucher generateVoucher(String originalBuyer, Voucher.VoucherType type, String comment) {
        return voucherRepository.save(new Voucher()
                .setOriginalBuyer(originalBuyer)
                .setComment(comment)
                .setType(type));
    }

    public boolean isValid(Voucher voucher) {
        if (voucher == null || voucher.getId() == null) {
            return false;
        } else {
            return voucherRepository.existsById(voucher.getId());
        }
    }

    public boolean isValid(String voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId);
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
        ParticipationData owner = participationRepository.findByVoucher(voucher);
        return owner == null || owner.getId().equals(participationDataId);
    }

    public void save(Voucher voucher) {
        voucherRepository.save(voucher);
    }

    public boolean canUseVoucher(String voucherId) {
        JelatynaPrincipal principal = SecurityContextUtil.getPrincipal();
        User user = userRepository.findById(principal.id);
        Voucher voucher = voucherRepository.findById(voucherId);

        if (user == null || user.getParticipationData() == null) {
            return canAssign(null, voucher);
        } else {
            String participationDataId = user.getParticipationData().getId();
            return canAssign(participationDataId, voucher);
        }
    }

    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }
}
