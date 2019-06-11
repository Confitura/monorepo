package pl.confitura.jelatyna.registration.voucher;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;


@RepositoryRestResource(path = "vouchers")
interface VoucherRepository extends Repository<Voucher, String> {

    @RestResource(exported = false)
    @PreAuthorize("@security.isAdmin()")
    Voucher save(Voucher token);

    @RestResource(exported = false)
    @PreAuthorize("@security.isAdmin()")
    List<Voucher> findAll();

    @RestResource(exported = false)
    boolean existsById(String token);

    @Query("SELECT v" +
           " FROM Voucher v" +
           " WHERE " +
           " v NOT IN (SELECT p.voucher FROM pl.confitura.jelatyna.registration.ParticipationData p)")
    @RestResource(exported = false)
    List<Voucher> findUnusedVouchers();

    Voucher findById(String id);
}
