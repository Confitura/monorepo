package pl.confitura.jelatyna.registration.voucher;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;


interface VoucherRepository extends Repository<Voucher, String> {

    @PreAuthorize("@security.isAdmin()")
    Voucher save(Voucher token);

    @PreAuthorize("@security.isAdmin()")
    List<Voucher> findAll();

    boolean existsById(String token);

    @Query("SELECT v" +
           " FROM Voucher v" +
           " WHERE " +
           " v NOT IN (SELECT p.voucher FROM pl.confitura.jelatyna.registration.ParticipationData p)")
    List<Voucher> findUnusedVouchers();

    Voucher findById(String id);

    @Query("SELECT v" +
            " FROM Voucher v" +
            " WHERE v.ticketSendDate is null")
    List<Voucher> findNotSent();
}
