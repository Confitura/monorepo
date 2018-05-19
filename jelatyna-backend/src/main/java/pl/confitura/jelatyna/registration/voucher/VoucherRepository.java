package pl.confitura.jelatyna.registration.voucher;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


@RepositoryRestResource(path = "vouchers")
interface VoucherRepository extends Repository<Voucher, String> {

    @RestResource(exported = false)
    @PreAuthorize("@security.isAdmin()")
    Voucher save(Voucher token);

    @PreAuthorize("@security.isAdmin()")
    List<Voucher> findAll();

    @RestResource(exported = false)
    @PreAuthorize("@security.isAdmin()")
    boolean existsById(String token);

    @Query("select t from Voucher t where t not in (select p.voucher from pl.confitura.jelatyna.registration.Participant p)")
    List<Voucher> findUnusedVouchers();
}
