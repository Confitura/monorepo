package pl.confitura.jelatyna.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "users")
public interface UserRepository extends JpaRepository<User, String> {
}
