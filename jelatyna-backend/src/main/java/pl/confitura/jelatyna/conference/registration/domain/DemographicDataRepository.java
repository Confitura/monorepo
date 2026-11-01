package pl.confitura.jelatyna.conference.registration.domain;

import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface DemographicDataRepository extends Repository<DemographicData, String> {

    DemographicData save(DemographicData token);

    @PreAuthorize("@security.isAdmin()")
    List<DemographicData> findAll();
}
