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

// A Day id is unique within its Edition (ADR-0004), so rooms, time slots and agenda entries are
// keyed by their Day's Edition too. The agenda must exist before that migration runs, so it
// migrates its own database in the shared container: up to the migration before it, then the rest.
class DayIdsPerEditionMigrationTest {

    private static final String DATABASE = "day_ids_per_edition";
    private static final PostgreSQLContainer postgres = BaseIntegrationTest.postgres;

    @BeforeAll
    static void migrateAnAgendaKeyedByGlobalDayIds() throws SQLException {
        postgres.start();
        try (Connection connection = connect(postgres.getDatabaseName());
             Statement statement = connection.createStatement()) {
            statement.execute("DROP DATABASE IF EXISTS " + DATABASE);
            statement.execute("CREATE DATABASE " + DATABASE);
        }
        flyway().target("7").load().migrate();
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    INSERT INTO agenda_day (id, date, display_order, label, edition_id)
                    SELECT 'day-1', '2026-09-25', 1, 'Day 1', id FROM edition WHERE year = 2026;
                    INSERT INTO agenda_room (id, display_order, label, day_id) VALUES ('room-1', 1, 'Room 1', 'day-1');
                    INSERT INTO agenda_time_slot (day_id, display_order, for_all_rooms, start_time, end_time) VALUES
                    ('day-1', 0, false, '09:00', '10:00'),
                    ('deleted-day', 0, false, '11:00', '12:00');
                    INSERT INTO agenda (id, label, room_id, time_slot_day_id, time_slot_display_order)
                    VALUES ('entry-1', 'Keynote', 'room-1', 'day-1', 0);
                    """);
        }
        flyway().load().migrate();
    }

    @Test
    void theAgendaKeepsItsRowsLinkedThroughTheirDaysEdition() throws SQLException {
        assertThat(rows("""
                SELECT entry.label || ' ' || room.label || ' ' || slot.start_time || ' ' || day.label || ' ' || edition.year
                FROM agenda entry
                JOIN agenda_time_slot slot ON slot.edition_id = entry.time_slot_edition_id
                    AND slot.day_id = entry.time_slot_day_id AND slot.display_order = entry.time_slot_display_order
                JOIN agenda_room room ON room.id = entry.room_id
                JOIN agenda_day day ON day.edition_id = slot.edition_id AND day.id = slot.day_id
                    AND day.edition_id = room.edition_id AND day.id = room.day_id
                JOIN edition ON edition.id = day.edition_id
                """)).containsExactly("Keynote Room 1 09:00:00 Day 1 2026");
    }

    @Test
    void aTimeSlotWhoseDayIsGoneStaysInTheCurrentEdition() throws SQLException {
        assertThat(rows("""
                SELECT slot.day_id || ' ' || slot.start_time FROM agenda_time_slot slot
                JOIN edition ON edition.id = slot.edition_id WHERE edition.current AND slot.day_id = 'deleted-day'
                """)).containsExactly("deleted-day 11:00:00");
    }

    @Test
    void aRoomNeedsItsDayInItsEdition() throws SQLException {
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement()) {
            assertThatThrownBy(() -> statement.execute(
                    "INSERT INTO agenda_room (id, display_order, day_id, edition_id) VALUES ('room-2', 1, 'day-1', 'no-edition')"))
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("foreign key");
        }
    }

    private static List<String> rows(String query) throws SQLException {
        List<String> rows = new ArrayList<>();
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(query)) {
            while (result.next()) {
                rows.add(result.getString(1));
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
