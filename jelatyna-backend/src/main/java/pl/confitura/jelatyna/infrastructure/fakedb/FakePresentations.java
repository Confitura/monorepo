package pl.confitura.jelatyna.infrastructure.fakedb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import pl.confitura.jelatyna.agenda.*;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.login.github.GithubService;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.util.List;
import java.util.Set;

import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_DB;

@Slf4j
@Configuration()
@Profile(FAKE_DB)
@RequiredArgsConstructor
@DependsOn({"agendaInitializer"})
public class FakePresentations {
    private final static Faker faker = new Faker();

    private final UserRepository userRepository;
    private final PresentationRepository presentationRepository;


    private final AgendaRepository agendaRepository;
    private final DayRepository dayRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        createFakeUsers();
    }

    public void createFakeUsers() {
        for (int i = 0; i < 40; i++) {
            var u = speaker(i);
            presentation(u);
        }
        List<Slot> slots = getSlots();
        for (int i = 0; i < 60; i++) {
            var u = speaker(i + 40);
            var p = presentation(u);
            p.setAccepted(true);
            p = presentationRepository.save(p);
            assignPresentationToSlot(p, slots, i);
        }
    }

    private void assignPresentationToSlot(Presentation p, List<Slot> slots, int i) {
        if (i >= slots.size()) {
            return;
        }
        AgendaEntry agendaEntry = new AgendaEntry();
        Slot slot = slots.get(i);
        agendaEntry.setTimeSlot(slot.timeSlot());
        if (slot.timeSlot.getDisplayOrder() % 2 == 0) {
            agendaEntry.setPresentation(p);
            agendaEntry.setRoom(slot.room());
        } else {
            agendaEntry.setLabel("Break");
        }
        agendaRepository.save(agendaEntry);
    }

    @NotNull
    private List<Slot> getSlots() {
        List<Room> rooms = roomRepository.findByDayId("day-1");
        List<TimeSlot> slots = timeSlotsRepository.findByIdDayId("day-1");
        return rooms.stream()
                .flatMap(room -> slots.stream().map(slot -> new Slot(slot, room)))
                .toList();
    }

    private User speaker(int i) {

        String fullName = faker.name().fullName();
        User user = new User()
                .setId("00000000-0000-0000-%d-000000000000".formatted(i))
                .setOrigin(GithubService.SYSTEM)
                .setName(fullName)
                .setEmail(fullName.replace(" ", ".") + "@example.com")
                .setBio(faker.text().text())
                .setWww(fullName.replace(" ", ".") + ".example.com")
                .setPhoto("https://picsum.photos/id/" + i + "/200/300")
                .setPrivacyPolicyAccepted(true);
        return userRepository.save(user);
    }

    private Presentation presentation(User user) {
        Presentation presentation = Presentation.from(new PresentationRequest(
                faker.movie().name(),
                faker.movie().quote(),
                faker.brooklynNineNine().quotes(),
                "begingner",
                "Polish", new String[]{"java"}), user, Set.of());
        return presentationRepository.save(presentation);
    }

    private record Slot(TimeSlot timeSlot, Room room) {
    }
}
