package pl.confitura.jelatyna;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Talks, workshops and agenda Days belong to an Edition, and the ones that existed before go to
// Edition 2026 (ADR-0004). They must exist before that migration runs, so it migrates its own
// database in the shared container: up to the migration before it, then the rest.
class TalksAndAgendaEditionMigrationTest {

    private static final String DATABASE = "talks_and_agenda_edition";
    private static final PostgreSQLContainer postgres = BaseIntegrationTest.postgres;

    @BeforeAll
    static void migrateTalksAndDaysCreatedBeforeEditions() throws SQLException {
        postgres.start();
        try (Connection connection = connect(postgres.getDatabaseName());
             Statement statement = connection.createStatement()) {
            statement.execute("DROP DATABASE IF EXISTS " + DATABASE);
            statement.execute("CREATE DATABASE " + DATABASE);
        }
        flyway().target("5").load().migrate();
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    INSERT INTO presentation (id, title, workshop) VALUES
                    ('talk', 'A talk', false),
                    ('workshop', 'A workshop', true)
                    """);
            statement.execute("""
                    INSERT INTO agenda_day (id, date, display_order, label) VALUES
                    ('day-1', '2026-09-25', 1, 'Day 1')
                    """);
        }
        flyway().load().migrate();
    }

    @Test
    void existingTalksAndWorkshopsAreInEdition2026() throws SQLException {
        assertThat(editionYears("presentation")).containsExactly("talk 2026", "workshop 2026");
    }

    @Test
    void existingAgendaDaysAreInEdition2026() throws SQLException {
        assertThat(editionYears("agenda_day")).containsExactly("day-1 2026");
    }

    @Test
    void aTalkNeedsAnEdition() throws SQLException {
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement()) {
            assertThatThrownBy(() -> statement.execute(
                    "INSERT INTO presentation (id, title, workshop) VALUES ('no-edition', 'A talk', false)"))
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("edition_id");
        }
    }

    @Test
    void anAgendaDayNeedsAnEdition() throws SQLException {
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement()) {
            assertThatThrownBy(() -> statement.execute(
                    "INSERT INTO agenda_day (id, date, display_order, label) VALUES ('no-edition', '2027-09-24', 1, 'Day 1')"))
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("edition_id");
        }
    }

    private static List<String> editionYears(String table) throws SQLException {
        List<String> rows = new ArrayList<>();
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(
                     "SELECT t.id, e.year FROM " + table + " t JOIN edition e ON e.id = t.edition_id ORDER BY t.id")) {
            while (result.next()) {
                rows.add(result.getString("id") + " " + result.getInt("year"));
            }
        }
        return rows;
    }

    private static FluentConfiguration flyway() {
        return Flyway.configure().dataSource(url(DATABASE), postgres.getUsername(), postgres.getPassword());
    }

    private static Connection connect(String database) throws SQLException {
        return DriverManager.getConnection(url(database), postgres.getUsername(), postgres.getPassword());
    }

    private static String url(String database) {
        return "jdbc:postgresql://%s:%d/%s".formatted(
                postgres.getHost(), postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), database);
    }
}
