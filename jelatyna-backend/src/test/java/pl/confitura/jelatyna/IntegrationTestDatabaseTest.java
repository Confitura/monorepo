package pl.confitura.jelatyna;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationTestDatabaseTest extends BaseIntegrationTest {

    @Autowired
    DataSource dataSource;

    @Test
    void integrationTestsRunOnProdsPostgresMajorVersion() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            assertThat(metaData.getDatabaseProductName()).isEqualTo("PostgreSQL");
            assertThat(metaData.getDatabaseMajorVersion()).isEqualTo(16);
        }
    }
}
