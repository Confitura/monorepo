package pl.confitura.jelatyna;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// The migration creating Editions makes Edition 2026 current with prod's C4P period (ADR-0004).
// fake-db starts reopen that period, so this migrates its own database in the shared container.
class EditionSeedMigrationTest {

    private static final String DATABASE = "edition_seed";
    private static final PostgreSQLContainer postgres = BaseIntegrationTest.postgres;

    @BeforeAll
    static void migrate() throws SQLException {
        postgres.start();
        try (Connection connection = connect(postgres.getDatabaseName());
             Statement statement = connection.createStatement()) {
            statement.execute("DROP DATABASE IF EXISTS " + DATABASE);
            statement.execute("CREATE DATABASE " + DATABASE);
        }
        Flyway.configure().dataSource(url(DATABASE), postgres.getUsername(), postgres.getPassword()).load().migrate();
    }

    @Test
    void createsEdition2026AsTheCurrentEdition() throws SQLException {
        try (Connection connection = connect(DATABASE);
             ResultSet edition = connection.createStatement().executeQuery("SELECT * FROM edition")) {
            assertThat(edition.next()).isTrue();
            assertThat(edition.getInt("year")).isEqualTo(2026);
            assertThat(edition.getString("name")).isEqualTo("Confitura 2026");
            assertThat(edition.getObject("conference_start", OffsetDateTime.class)).isAtSameInstantAs(warsaw("2026-09-25T09:00:00"));
            assertThat(edition.getObject("last_day", LocalDate.class)).isEqualTo(LocalDate.of(2026, 9, 26));
            assertThat(edition.getObject("c4p_start", OffsetDateTime.class)).isAtSameInstantAs(warsaw("2026-02-01T00:00:00"));
            assertThat(edition.getObject("c4p_end", OffsetDateTime.class)).isAtSameInstantAs(warsaw("2026-08-06T23:59:59"));
            assertThat(edition.getBoolean("current")).isTrue();
            assertThat(edition.next()).isFalse();
        }
    }

    @Test
    void onlyOneEditionIsCurrent() throws SQLException {
        try (Connection connection = connect(DATABASE)) {
            assertThatThrownBy(() -> connection.createStatement().execute("""
                    INSERT INTO edition (id, year, name, conference_start, last_day, current)
                    VALUES ('2027', 2027, 'Confitura 2027', now(), current_date, true)
                    """))
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("edition_current_key");
        }
    }

    private static OffsetDateTime warsaw(String localDateTime) {
        return LocalDateTime.parse(localDateTime).atZone(ZoneId.of("Europe/Warsaw")).toOffsetDateTime();
    }

    private static Connection connect(String database) throws SQLException {
        return DriverManager.getConnection(url(database), postgres.getUsername(), postgres.getPassword());
    }

    private static String url(String database) {
        return "jdbc:postgresql://%s:%d/%s".formatted(
                postgres.getHost(), postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), database);
    }
}
