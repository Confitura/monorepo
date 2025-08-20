package pl.confitura.jelatyna.agenda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.agenda.TimeSlot.TimeSlotId;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;

import java.time.LocalDate;
import java.time.LocalTime;

import static java.util.Collections.emptyList;

@Component
@Slf4j
@RequiredArgsConstructor
public class AgendaInitializer {

    private final DayRepository dayRepository;
    private final RoomRepository roomRepository;
    private final TimeSlotsRepository timeSlotsRepository;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        init();
    }

    private void init() {
        loginAsAdmin();
        if (!dayRepository.findAll().isEmpty()) {
            log.info("Agenda already initialized");
            return;
        }
        log.info("Initializing agenda");

        // Create two days
        Day day1 = new Day()
                .setId("day-1")
                .setLabel("Day 1")
                .setDate(LocalDate.of(2025, 8, 1))
                .setDisplayOrder(1);

        Day day2 = new Day()
                .setId("day-2")
                .setLabel("Day 2")
                .setDate(LocalDate.of(2025, 8, 2))
                .setDisplayOrder(2);

        day1 = dayRepository.save(day1);
        day2 = dayRepository.save(day2);

        // Create three rooms
        Room room1 = new Room()
                .setId("room-1")
                .setLabel("Main Hall")
                .setDisplayOrder(1)
                .setDay(day1);

        Room room2 = new Room()
                .setId("room-2")
                .setLabel("Workshop Room")
                .setDisplayOrder(2)
                .setDay(day1);

        Room room3 = new Room()
                .setId("room-3")
                .setLabel("Conference Room")
                .setDisplayOrder(3)
                .setDay(day1);

        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        // Create time slots
        TimeSlot slot1 = new TimeSlot()
                .setStart(LocalTime.of(9, 0))
                .setEnd(LocalTime.of(10, 0))
                .setId(getTimeSlotId(day1, 1));

        TimeSlot slot2 = new TimeSlot()
                .setStart(LocalTime.of(10, 15))
                .setEnd(LocalTime.of(11, 15))
                .setId(getTimeSlotId(day1, 2));

        TimeSlot slot3 = new TimeSlot()
                .setStart(LocalTime.of(11, 30))
                .setEnd(LocalTime.of(12, 30))
                .setId(getTimeSlotId(day1, 3));

        TimeSlot lunchSlot = new TimeSlot()
                .setStart(LocalTime.of(12, 30))
                .setEnd(LocalTime.of(13, 30))
                .setForAllRooms(true)
                .setId(getTimeSlotId(day1, 4));

        TimeSlot slot4 = new TimeSlot()
                .setStart(LocalTime.of(13, 30))
                .setEnd(LocalTime.of(14, 30))
                .setId(getTimeSlotId(day1, 5));
        TimeSlot slot5 = new TimeSlot()
                .setStart(LocalTime.of(14, 45))
                .setEnd(LocalTime.of(15, 45))
                .setId(getTimeSlotId(day1, 6));

        TimeSlot slot6 = new TimeSlot()
                .setStart(LocalTime.of(16, 0))
                .setEnd(LocalTime.of(17, 0))
                .setId(getTimeSlotId(day1, 7));

        timeSlotsRepository.save(slot1);
        timeSlotsRepository.save(slot2);
        timeSlotsRepository.save(slot3);
        timeSlotsRepository.save(lunchSlot);
        timeSlotsRepository.save(slot4);
        timeSlotsRepository.save(slot5);
        timeSlotsRepository.save(slot6);


        // Create time slots for Day 2
        TimeSlot d2slot1 = new TimeSlot()
                .setStart(LocalTime.of(9, 0))
                .setEnd(LocalTime.of(10, 0))
                .setId(getTimeSlotId(day2, 1));

        TimeSlot d2slot2 = new TimeSlot()
                .setStart(LocalTime.of(10, 15))
                .setEnd(LocalTime.of(11, 15))
                .setId(getTimeSlotId(day2, 2));

        TimeSlot d2slot3 = new TimeSlot()
                .setStart(LocalTime.of(11, 30))
                .setEnd(LocalTime.of(12, 30))
                .setId(getTimeSlotId(day2, 3));

        TimeSlot d2lunchSlot = new TimeSlot()
                .setStart(LocalTime.of(12, 30))
                .setEnd(LocalTime.of(13, 30))
                .setForAllRooms(true)
                .setId(getTimeSlotId(day2, 4));

        TimeSlot d2slot4 = new TimeSlot()
                .setStart(LocalTime.of(13, 30))
                .setEnd(LocalTime.of(14, 30))
                .setId(getTimeSlotId(day2, 5));
        TimeSlot d2slot5 = new TimeSlot()
                .setStart(LocalTime.of(14, 45))
                .setEnd(LocalTime.of(15, 45))
                .setId(getTimeSlotId(day2, 6));

        TimeSlot d2slot6 = new TimeSlot()
                .setStart(LocalTime.of(16, 0))
                .setEnd(LocalTime.of(17, 0))
                .setId(getTimeSlotId(day2, 7));

        timeSlotsRepository.save(d2slot1);
        timeSlotsRepository.save(d2slot2);
        timeSlotsRepository.save(d2slot3);
        timeSlotsRepository.save(d2lunchSlot);
        timeSlotsRepository.save(d2slot4);
        timeSlotsRepository.save(d2slot5);
        timeSlotsRepository.save(d2slot6);


    }

    @NotNull
    private static TimeSlotId getTimeSlotId(Day day, int displayOrder) {
        return new TimeSlotId(day.getId(), displayOrder);
    }

    private static void loginAsAdmin() {
        JelatynaPrincipal principal = new JelatynaPrincipal().setId("system").setName("system").setAdmin(true);
        var token = new PreAuthenticatedAuthenticationToken(principal, "", emptyList());
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
