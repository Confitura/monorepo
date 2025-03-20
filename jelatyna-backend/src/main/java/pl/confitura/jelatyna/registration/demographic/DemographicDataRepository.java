package pl.confitura.jelatyna.registration.demographic;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


@RepositoryRestResource(path = "demographicData")
public interface DemographicDataRepository extends Repository<DemographicData, String> {

    @RestResource(exported = false)
    DemographicData save(DemographicData token);

    @PreAuthorize("@security.isAdmin()")
    List<DemographicData> findAll();
}
