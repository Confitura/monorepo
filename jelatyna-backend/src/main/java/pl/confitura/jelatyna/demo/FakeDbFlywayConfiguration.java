package pl.confitura.jelatyna.demo;

import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static pl.confitura.jelatyna.platform.Profiles.FAKE_DB;

@Configuration
@Profile(FAKE_DB)
public class FakeDbFlywayConfiguration {

    // The fake-data seeders insert unconditionally, so every start needs an empty schema.
    // Clean is enabled only in application-fake-db.yml; Flyway refuses it everywhere else.
    @Bean
    FlywayMigrationStrategy cleanThenMigrate() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}
