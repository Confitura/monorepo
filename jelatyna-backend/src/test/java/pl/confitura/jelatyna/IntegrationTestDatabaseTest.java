package pl.confitura.jelatyna;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationTestDatabaseTest extends BaseIntegrationTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    Flyway flyway;

    @Test
    void integrationTestsRunOnProdsPostgresMajorVersion() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            assertThat(metaData.getDatabaseProductName()).isEqualTo("PostgreSQL");
            assertThat(metaData.getDatabaseMajorVersion()).isEqualTo(16);
        }
    }

    @Test
    void schemaIsBuiltByEveryFlywayMigration() {
        MigrationInfo current = flyway.info().current();
        assertThat(current.getState()).isEqualTo(MigrationState.SUCCESS);
        assertThat(flyway.info().pending()).isEmpty();
    }

    @Test
    void legacyTablesAndColumnsNoEntityMapsAreDropped() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            for (String table : List.of("admin", "news", "presentation_ratings", "rate", "tag",
                    "users_performed_rate", "users_personal_agenda", "volunteer")) {
                try (ResultSet tables = metaData.getTables(null, "public", table, null)) {
                    assertThat(tables.next()).as("table %s", table).isFalse();
                }
            }
            try (ResultSet columns = metaData.getColumns(null, "public", "faq_entry", "category")) {
                assertThat(columns.next()).as("column faq_entry.category").isFalse();
            }
        }
    }

    // Logins are the only record of how a User signs in (ADR-0003).
    @Test
    void usersNoLongerHoldTheirSignInNowThatLoginsDo() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            for (String column : List.of("social_id", "origin")) {
                try (ResultSet columns = connection.getMetaData().getColumns(null, "public", "users", column)) {
                    assertThat(columns.next()).as("column users.%s", column).isFalse();
                }
            }
        }
    }
}
