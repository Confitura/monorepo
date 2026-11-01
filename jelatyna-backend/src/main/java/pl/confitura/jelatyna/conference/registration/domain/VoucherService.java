package pl.confitura.jelatyna.conference.registration.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final ParticipationRepository participationRepository;

    public Voucher generateVoucher(String originalBuyer) {
        return generateVoucher(originalBuyer, Voucher.VoucherType.PARTICIPANT, null);
    }

    public Voucher generateVoucher(String originalBuyer, Voucher.VoucherType type, String comment) {
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

    public boolean isUsed(Voucher voucher) {
        return participationRepository.findByVoucher(voucher) != null;
    }

    public boolean canUseVoucher(String voucherId) {
        return getParticipationDataFor(voucherId) == null;
    }

    public ParticipationData getParticipationDataFor(String voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId);
        return participationRepository.findByVoucher(voucher);
    }

    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }
}
