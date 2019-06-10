package pl.confitura.jelatyna.registration.voucher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.infrastructure.security.SecurityContextUtil;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.ParticipationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;

    public Voucher generateVoucher(String originalBuyer) {
        return voucherRepository.save(new Voucher().setOriginalBuyer(originalBuyer));
    }

    public boolean isValid(Voucher voucher) {
        if (voucher == null || voucher.getId() == null) {
            return false;
        } else {
            return voucherRepository.existsById(voucher.getId());
        }
    }

    boolean isValid(String voucherId) {
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

    public boolean isUsed(Voucher voucher) {
        return participationRepository.findByVoucher(voucher) != null;
    }

    public boolean canUseVoucher(String voucherId) {
        Voucher v = voucherRepository.findById(voucherId);
        return participationRepository.findByVoucher(v) == null;
    }
}
