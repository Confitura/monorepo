package pl.confitura.jelatyna.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

// A lightweight runner instead of a full second Spring context: the bean is absent, so no dependencies are needed.
class AgendaInitializerProfileTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(AgendaInitializer.class);

    @Test
    void isNotRegisteredWithoutFakeDbProfile() {
        runner.withInitializer(c -> c.getEnvironment().setActiveProfiles("prod"))
                .run(context -> assertThat(context).doesNotHaveBean(AgendaInitializer.class));
    }
}
