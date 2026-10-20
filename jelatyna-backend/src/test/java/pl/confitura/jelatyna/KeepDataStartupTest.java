package pl.confitura.jelatyna;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.postgresql.PostgreSQLContainer;
import pl.confitura.jelatyna.infrastructure.fakedb.FakeLoginUsers;
import pl.confitura.jelatyna.login.github.GithubService;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// The keep-data local mode (-Dspring-boot.run.profiles=local,fake-security). It does not extend
// BaseIntegrationTest so the test profile stays off: only application.yml and application-local.yml
// configure the application, as under spring-boot:run. The datasource is the shared container.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles({"local", "fake-security"})
// BEFORE_CLASS makes the application start after leaveATableFromAPreviousRun() has run.
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class KeepDataStartupTest {

    private static final String LEFTOVER_TABLE = "kept_from_previous_run";

    @ServiceConnection
    static final PostgreSQLContainer postgres = BaseIntegrationTest.postgres;

    @Autowired
    DataSource dataSource;

    @Autowired
    Flyway flyway;

    @Autowired
    FakeLoginUsers fakeLoginUsers;

    @Autowired
    PresentationRepository presentationRepository;

    @BeforeAll
    static void leaveATableFromAPreviousRun() throws SQLException {
        postgres.start();
        // A previous run leaves a migrated schema; Flyway refuses a non-empty schema without its history.
        Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .load()
                .migrate();
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + LEFTOVER_TABLE + " (id integer)");
        }
    }

    @Test
    void startingUnderLocalKeepsWhatAPreviousRunLeftBehind() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             ResultSet tables = connection.getMetaData().getTables(null, "public", LEFTOVER_TABLE, null)) {
            assertThat(tables.next()).isTrue();
        }
    }

    @Test
    void localProfileRefusesToCleanTheDatabase() {
        assertThatThrownBy(() -> flyway.clean()).isInstanceOf(FlywayException.class);
    }

    @Test
    void restartingKeepsTheFakeSpeakerAtOneTalkAndOneWorkshop() {
        fakeLoginUsers.createFakeUsers();

        User speaker = fakeLoginUsers.getBySystem(GithubService.SYSTEM);
        assertThat(presentationRepository.findBySpeakersContains(speaker))
                .extracting(Presentation::isWorkshop)
                .containsExactlyInAnyOrder(false, true);
    }
}
