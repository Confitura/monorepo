package pl.confitura.jelatyna.infrastructure.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.Room;
import pl.confitura.jelatyna.agenda.RoomRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;

import static org.assertj.core.api.Assertions.assertThat;

class JpaAuditingTest extends BaseIntegrationTest {

    @Autowired
    RoomRepository repository;

    @Test
    @Transactional
    void shouldSaveAuditInfo() {
        SecurityHelper.asAdmin();

        Room saved = repository.save(new Room().setDisplayOrder(0));

        assertThat(saved).isNotNull();
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getLastModifiedDate()).isNotNull();
        assertThat(saved.getCreatedBy()).isEqualTo(SecurityHelper.ADMIN.getId());
        assertThat(saved.getLastModifiedBy()).isEqualTo(SecurityHelper.ADMIN.getId());
    }
}