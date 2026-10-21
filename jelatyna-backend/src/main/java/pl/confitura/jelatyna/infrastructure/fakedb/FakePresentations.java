package pl.confitura.jelatyna.infrastructure.fakedb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import pl.confitura.jelatyna.agenda.*;
import pl.confitura.jelatyna.agenda.TimeSlot.TimeSlotId;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.edition.EditionRepository;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_DB;

@Slf4j
@Configuration()
@Profile(FAKE_DB)
@RequiredArgsConstructor
@DependsOn({"agendaInitializer"})
public class FakePresentations {
    private static final String TODAY_DAY_ID = "day-today";
    private final static Faker faker = new Faker();

    private final UserRepository userRepository;
    private final PresentationRepository presentationRepository;
    private final AgendaRepository agendaRepository;
    private final DayRepository dayRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;
    private final EditionRepository editionRepository;

    @Transactional
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        loginAsAdmin();
        openTheCallForPapers();
        clearExistingData();
        createTodaySchedule();
        createPresentations();
    }

    private void loginAsAdmin() {
        JelatynaPrincipal principal = new JelatynaPrincipal()
                .setId("system").setName("system").setAdmin(true);
        SecurityContextHolder.getContext().setAuthentication(
                new PreAuthenticatedAuthenticationToken(principal, "", Collections.emptyList())
        );
    }

    // The migration gives the Current Edition prod's C4P period; open it so the fake speaker can add talks.
    private void openTheCallForPapers() {
        Instant now = Instant.now();
        editionRepository.save(editionRepository.findCurrent()
                .setC4pStart(now.minus(365, DAYS))
                .setC4pEnd(now.plus(365, DAYS)));
    }

    private void clearExistingData() {
        agendaRepository.findAll().forEach(e -> agendaRepository.deleteById(e.getId()));
        roomRepository.findAll().forEach(r -> roomRepository.deleteById(r.getId()));
        timeSlotsRepository.findAll().forEach(ts -> timeSlotsRepository.deleteById(ts.getId()));
        dayRepository.findAll().forEach(d -> dayRepository.deleteById(d.getId()));
    }

    private void createTodaySchedule() {
        LocalTime now = LocalTime.now();

        Day today = dayRepository.save(new Day()
                .setId(TODAY_DAY_ID)
                .setLabel("Today")
                .setDate(LocalDate.now())
                .setDisplayOrder(1));

        createRoom("today-a", "Room A", 1, today);
        createRoom("today-b", "Room B", 2, today);
        createRoom("today-c", "Room C", 3, today);

        // 4 talk slots (even displayOrder) + 3 break slots (odd displayOrder)
        // now falls inside slot 2 — the current talk
        createTimeSlot(TODAY_DAY_ID, 0, now.minusMinutes(100), now.minusMinutes(50), false);
        createTimeSlot(TODAY_DAY_ID, 1, now.minusMinutes(50),  now.minusMinutes(40), true);
        createTimeSlot(TODAY_DAY_ID, 2, now.minusMinutes(40),  now.plusMinutes(20),  false); // CURRENT
        createTimeSlot(TODAY_DAY_ID, 3, now.plusMinutes(20),   now.plusMinutes(30),  true);
        createTimeSlot(TODAY_DAY_ID, 4, now.plusMinutes(30),   now.plusMinutes(90),  false); // NEXT
        createTimeSlot(TODAY_DAY_ID, 5, now.plusMinutes(90),   now.plusMinutes(100), true);
        createTimeSlot(TODAY_DAY_ID, 6, now.plusMinutes(100),  now.plusMinutes(160), false); // FUTURE

        createBreakEntry(TODAY_DAY_ID, 1, "Coffee Break");
        createBreakEntry(TODAY_DAY_ID, 3, "Short Break");
        createBreakEntry(TODAY_DAY_ID, 5, "Lunch Break");
    }

    private void createTimeSlot(String dayId, int displayOrder, LocalTime start, LocalTime end, boolean forAllRooms) {
        timeSlotsRepository.save(new TimeSlot()
                .setId(new TimeSlotId(dayId, displayOrder))
                .setStart(start)
                .setEnd(end)
                .setForAllRooms(forAllRooms));
    }

    private void createBreakEntry(String dayId, int displayOrder, String label) {
        TimeSlot breakSlot = timeSlotsRepository.findById(new TimeSlotId(dayId, displayOrder));
        AgendaEntry entry = new AgendaEntry();
        entry.setTimeSlot(breakSlot);
        entry.setLabel(label);
        agendaRepository.save(entry);
    }

    private Room createRoom(String id, String label, int displayOrder, Day day) {
        return roomRepository.save(new Room()
                .setId(id)
                .setLabel(label)
                .setDisplayOrder(displayOrder)
                .setDay(day));
    }

    private void createPresentations() {
        for (int i = 0; i < 40; i++) {
            presentation(speaker(i));
        }
        List<Slot> slots = getTalkSlots();
        for (int i = 0; i < 60; i++) {
            var u = speaker(i + 40);
            var p = presentation(u);
            p.setAccepted(true);
            p = presentationRepository.save(p);
            if (i < slots.size()) {
                assignPresentationToSlot(p, slots.get(i));
            }
        }
    }

    private List<Slot> getTalkSlots() {
        List<Room> rooms = roomRepository.findByDayId(TODAY_DAY_ID);
        List<TimeSlot> talkSlots = timeSlotsRepository.findByIdDayId(TODAY_DAY_ID)
                .stream()
                .filter(ts -> !ts.isForAllRooms())
                .toList();
        return rooms.stream()
                .flatMap(room -> talkSlots.stream().map(slot -> new Slot(slot, room)))
                .toList();
    }

    private void assignPresentationToSlot(Presentation p, Slot slot) {
        AgendaEntry agendaEntry = new AgendaEntry();
        agendaEntry.setTimeSlot(slot.timeSlot());
        agendaEntry.setPresentation(p);
        agendaEntry.setRoom(slot.room());
        agendaRepository.save(agendaEntry);
    }

    private User speaker(int i) {
        String fullName = faker.name().fullName();
        return userRepository.save(new User()
                .setId("00000000-0000-0000-%d-000000000000".formatted(i))
                .setName(fullName)
                .setEmail(fullName.replace(" ", ".") + "@example.com")
                .setBio(faker.text().text())
                .setWww(fullName.replace(" ", ".") + ".example.com")
                .setPhoto("https://picsum.photos/id/" + i + "/200/300")
                .setPrivacyPolicyAccepted(true));
    }

    private Presentation presentation(User user) {
        return presentationRepository.save(Presentation.from(new PresentationRequest(
                faker.movie().name(),
                faker.movie().quote(),
                faker.brooklynNineNine().quotes(),
                "beginner",
                "Polish",
                new String[]{"java"}), user, Set.of()));
    }

    private record Slot(TimeSlot timeSlot, Room room) {
    }
}
