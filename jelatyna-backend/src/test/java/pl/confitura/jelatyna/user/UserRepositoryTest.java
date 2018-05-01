package pl.confitura.jelatyna.user;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test, fake-db")
@Transactional
public class UserRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    public void cleanup() {
        em.createQuery("DELETE FROM User").executeUpdate();
    }

    @Test
    void should_save_a_user() {
        User user = new User()
                .setOrigin("twitter")
                .setSocialId("1234");

        User saved = repository.save(user);

        assertThat(repository.findById(saved.getId()))
                .isEqualToComparingOnlyGivenFields(user, "origin", "socialId");
    }

    @Test
    void should_find_a_user_by_social_id() {
        User user = repository.save(new User()
                .setOrigin("twitter")
                .setSocialId("1"));
        repository.save(new User()
                .setOrigin("twitter")
                .setSocialId("2"));

        User found = repository.findBySocialId("1");

        assertThat(found).isEqualTo(user);
    }

    @Test
    void should_check_if_exists_by_social_id() {
        repository.save(new User()
                .setOrigin("twitter")
                .setSocialId("1"));

        assertThat(repository
                .existsBySocialId("1")).isTrue();
        assertThat(repository
                .existsBySocialId("2")).isFalse();
    }
}