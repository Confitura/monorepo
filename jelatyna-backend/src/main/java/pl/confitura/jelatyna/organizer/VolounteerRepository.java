package pl.confitura.jelatyna.organizer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "volunteers")
public interface VolounteerRepository extends JpaRepository<Volunteer, Long>{
}
