package pl.confitura.jelatyna.registration.demographic;

import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface DemographicDataRepository extends Repository<DemographicData, String> {

    DemographicData save(DemographicData token);

    @PreAuthorize("@security.isAdmin()")
    List<DemographicData> findAll();
}
