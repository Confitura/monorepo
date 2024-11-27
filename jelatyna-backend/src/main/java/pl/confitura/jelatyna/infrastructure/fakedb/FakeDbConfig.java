package pl.confitura.jelatyna.infrastructure.fakedb;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_DB;

@Slf4j
@Configuration
@Profile(FAKE_DB)
public class FakeDbConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }
}
