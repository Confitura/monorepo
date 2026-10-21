package pl.confitura.jelatyna.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

public interface LoginRepository extends Repository<Login, String> {

    Login save(Login login);

    void delete(Login login);

    // Fetches the User too: sign-in hands it on to build the token.
    @Query("FROM Login l JOIN FETCH l.user WHERE l.provider = :provider AND l.externalId = :externalId")
    Optional<Login> findByProviderAndExternalId(@Param("provider") String provider,
                                                @Param("externalId") String externalId);

    @Query("FROM Login WHERE user.id = :userId ORDER BY createdDate")
    List<Login> findByUserId(@Param("userId") String userId);

    @Query("FROM Login ORDER BY provider")
    @PreAuthorize("@security.isAdmin()")
    List<Login> findAll();
}
