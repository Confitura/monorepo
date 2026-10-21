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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// The login table is created and backfilled from users.social_id (ADR-0003). Users must exist
// before that migration runs, so it migrates its own database in the shared container: up to the
// migration before it, then the rest.
class LoginBackfillMigrationTest {

    private static final String DATABASE = "login_backfill";
    private static final PostgreSQLContainer postgres = BaseIntegrationTest.postgres;

    @BeforeAll
    static void migrateUsersCreatedBeforeLogins() throws SQLException {
        postgres.start();
        try (Connection connection = connect(postgres.getDatabaseName());
             Statement statement = connection.createStatement()) {
            statement.execute("DROP DATABASE IF EXISTS " + DATABASE);
            statement.execute("CREATE DATABASE " + DATABASE);
        }
        flyway().target("2").load().migrate();
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    INSERT INTO users (id, is_admin, is_volunteer, origin, social_id, created_date) VALUES
                    ('github-user', false, false, 'github', 'Z2l0aHViLzEyMTk2NTc=', '2019-05-01 10:00:00'),
                    ('google-user', false, false, 'google', 'Z29vZ2xlLzEyMzQ1Njc4OTA=', '2020-06-02 11:00:00'),
                    ('facebook-user', false, false, 'facebook', 'ZmFjZWJvb2svMTAxNTY3ODk=', '2021-07-03 12:00:00'),
                    ('twitter-user', false, false, 'twitter', 'dHdpdHRlci9jb25maXR1cmE=', NULL),
                    ('manual-user', false, false, 'manual', NULL, '2022-08-04 13:00:00'),
                    ('blank-social-id-user', false, false, 'manual', '', '2023-09-05 14:00:00')
                    """);
        }
        flyway().load().migrate();
    }

    @Test
    void backfillsOneDecodedLoginPerNonBlankSocialId() throws SQLException {
        List<List<Object>> logins = new ArrayList<>();
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(
                     "SELECT user_id, provider, external_id, created_date FROM login ORDER BY user_id")) {
            while (rows.next()) {
                logins.add(Arrays.asList(
                        rows.getString("user_id"),
                        rows.getString("provider"),
                        rows.getString("external_id"),
                        rows.getObject("created_date", LocalDateTime.class)));
            }
        }

        assertThat(logins).containsExactly(
                Arrays.asList("facebook-user", "facebook", "10156789", LocalDateTime.of(2021, 7, 3, 12, 0)),
                Arrays.asList("github-user", "github", "1219657", LocalDateTime.of(2019, 5, 1, 10, 0)),
                Arrays.asList("google-user", "google", "1234567890", LocalDateTime.of(2020, 6, 2, 11, 0)),
                Arrays.asList("twitter-user", "twitter", "confitura", null));
    }

    @Test
    void aProviderIdSignsInAsOnlyOneUser() throws SQLException {
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement()) {
            assertThatThrownBy(() -> statement.execute("""
                    INSERT INTO login (id, user_id, provider, external_id)
                    VALUES ('duplicate', 'manual-user', 'github', '1219657')
                    """))
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("login_provider_external_id_key");
        }
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
