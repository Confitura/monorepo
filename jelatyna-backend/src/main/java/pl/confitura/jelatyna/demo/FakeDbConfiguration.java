package pl.confitura.jelatyna.demo;

import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import pl.confitura.jelatyna.conference.agenda.domain.AgendaRepository;
import pl.confitura.jelatyna.conference.agenda.domain.DayRepository;
import pl.confitura.jelatyna.conference.agenda.domain.RoomRepository;
import pl.confitura.jelatyna.conference.agenda.domain.TimeSlotsRepository;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

import static pl.confitura.jelatyna.platform.Profiles.FAKE_DB;

@Configuration
@Profile(FAKE_DB)
class FakeDbConfiguration {

    // The fake-data seeders insert unconditionally, so every start needs an empty schema.
    // Clean is enabled only in application-fake-db.yml; Flyway refuses it everywhere else.
    @Bean
    FlywayMigrationStrategy cleanThenMigrate() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }

    @Bean
    AgendaInitializer agendaInitializer(DayRepository dayRepository, RoomRepository roomRepository,
                                        TimeSlotsRepository timeSlotsRepository, EditionRepository editionRepository) {
        return new AgendaInitializer(dayRepository, roomRepository, timeSlotsRepository, editionRepository);
    }

    @Bean
    @DependsOn("agendaInitializer")
    FakePresentations fakePresentations(UserRepository userRepository, PresentationRepository presentationRepository,
                                        AgendaRepository agendaRepository, DayRepository dayRepository,
                                        TimeSlotsRepository timeSlotsRepository, RoomRepository roomRepository,
                                        EditionRepository editionRepository) {
        return new FakePresentations(userRepository, presentationRepository, agendaRepository, dayRepository,
                timeSlotsRepository, roomRepository, editionRepository);
    }
}
