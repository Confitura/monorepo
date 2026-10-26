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

// Two vote-for-papers starts at once used to give a voter two votes per talk. The duplicates must exist
// before the migration that removes them runs, so it migrates its own database in the shared container:
// up to the migration before it, then the rest.
class OneVotePerTalkMigrationTest {

    private static final String DATABASE = "one_vote_per_talk";
    private static final PostgreSQLContainer postgres = BaseIntegrationTest.postgres;

    @BeforeAll
    static void migrateVotersWithDuplicateVotes() throws SQLException {
        postgres.start();
        try (Connection connection = connect(postgres.getDatabaseName());
             Statement statement = connection.createStatement()) {
            statement.execute("DROP DATABASE IF EXISTS " + DATABASE);
            statement.execute("CREATE DATABASE " + DATABASE);
        }
        flyway().target("9").load().migrate();
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    INSERT INTO presentation (id, workshop, edition_id) SELECT 'talk', false, id FROM edition WHERE year = 2026;
                    INSERT INTO vote (id, token, presentation_id, vote_order, rate, vote_date) VALUES
                    ('rated-earlier', 'rated', 'talk', 3, 1, '2026-06-01 10:00'),
                    ('rated-latest', 'rated', 'talk', 5, 0, '2026-06-01 11:00'),
                    ('rated-not', 'rated', 'talk', 0, NULL, NULL),
                    ('unrated-later', 'unrated', 'talk', 4, NULL, NULL),
                    ('unrated-first', 'unrated', 'talk', 2, NULL, NULL),
                    ('single', 'single', 'talk', 1, NULL, NULL),
                    ('no-token', NULL, 'talk', 0, NULL, NULL),
                    ('no-token-either', NULL, 'talk', 1, NULL, NULL);
                    """);
        }
        flyway().load().migrate();
    }

    @Test
    void aVoterKeepsTheirLatestRatedVoteForATalk() throws SQLException {
        assertThat(rows("SELECT id FROM vote WHERE token = 'rated'")).containsExactly("rated-latest");
    }

    @Test
    void aVoterWhoRatedNoCopyKeepsTheFirstInTheirBallot() throws SQLException {
        assertThat(rows("SELECT id FROM vote WHERE token = 'unrated'")).containsExactly("unrated-first");
    }

    @Test
    void aVoteWithoutDuplicatesStays() throws SQLException {
        assertThat(rows("SELECT id FROM vote WHERE token = 'single'")).containsExactly("single");
    }

    // The unique index never compares NULLs, so votes without a token are not duplicates of each other.
    @Test
    void votesWithoutATokenStay() throws SQLException {
        assertThat(rows("SELECT id FROM vote WHERE token IS NULL")).containsExactlyInAnyOrder("no-token", "no-token-either");
    }

    @Test
    void aVoterCannotGetASecondVoteForATalk() throws SQLException {
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement()) {
            assertThatThrownBy(() -> statement.execute(
                    "INSERT INTO vote (id, token, presentation_id, vote_order) VALUES ('again', 'single', 'talk', 0)"))
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("duplicate key");
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
