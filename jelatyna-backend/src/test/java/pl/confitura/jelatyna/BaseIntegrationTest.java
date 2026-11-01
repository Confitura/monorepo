package pl.confitura.jelatyna;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.postgresql.PostgreSQLContainer;
import pl.confitura.jelatyna.identity.user.web.SecurityHelper;
import pl.confitura.jelatyna.conference.dashboard.listmonk.TestListMongConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles({"test", "fake-db"})
@Transactional(propagation = Propagation.NEVER)
@Import(TestListMongConfiguration.class)
public class BaseIntegrationTest {

    // Same PostgreSQL version as prod. Static and not a bean, so every cached Spring
    // context shares this one container; Boot starts it on first use.
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16.10");

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        SecurityHelper.cleanSecurity();
    }

    public String json(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JacksonException e) {
            return "";
        }
    }

    protected <T> T fromJson(String s, Class<T> valueType) {
        try {
            return objectMapper.readValue(s, valueType);
        } catch (JacksonException e) {
            return null;
        }
    }
}
