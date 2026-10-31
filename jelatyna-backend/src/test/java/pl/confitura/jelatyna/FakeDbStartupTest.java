package pl.confitura.jelatyna;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import pl.confitura.jelatyna.agenda.DayRepository;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

// BEFORE_CLASS makes the application start after leaveATableFromAPreviousRun() has run.
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class FakeDbStartupTest extends BaseIntegrationTest {

    private static final String LEFTOVER_TABLE = "leftover_from_previous_run";

    @Autowired
    DataSource dataSource;

    @Autowired
    EditionRepository editionRepository;

    @Autowired
    PresentationRepository presentationRepository;

    @Autowired
    DayRepository dayRepository;

    @BeforeAll
    static void leaveATableFromAPreviousRun() throws SQLException {
        postgres.start();
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + LEFTOVER_TABLE + " (id integer)");
        }
    }

    @Test
    void startingUnderFakeDbWipesWhatAPreviousRunLeftBehind() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             ResultSet tables = connection.getMetaData().getTables(null, "public", LEFTOVER_TABLE, null)) {
            assertThat(tables.next()).isFalse();
        }
    }

    @Test
    void startingUnderFakeDbOpensTheCurrentEditionsCallForPapers() {
        assertThat(editionRepository.findCurrent().isC4pOpen()).isTrue();
    }

    @Test
    void startingUnderFakeDbPutsTheFakeTalksAndAgendaInTheCurrentEdition() {
        String current = editionRepository.findCurrent().getId();
        SecurityHelper.asAdmin();

        assertThat(presentationRepository.findAll()).isNotEmpty()
                .allSatisfy(talk -> assertThat(talk.getEdition().getId()).isEqualTo(current));
        assertThat(dayRepository.findAll()).isNotEmpty()
                .allSatisfy(day -> assertThat(day.getEdition().getId()).isEqualTo(current));
    }
}
