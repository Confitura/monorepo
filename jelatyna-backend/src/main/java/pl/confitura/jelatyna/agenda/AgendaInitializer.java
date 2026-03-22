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
import java.util.ArrayList;
import java.util.List;

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
        if (dayRepository.findAll().isEmpty()) {
            log.info("Initializing agenda");
            createDayOne();
            createDayTwo();
            createDayOneWorkshops();
            createDayTwoWorkshops();
        }

    }

    private void createDayOneWorkshops() {
        Day day = dayRepository.save(new Day()
                .setId("day-1-workshops")
                .setLabel("Day 1 Workshops")
                .setDate(LocalDate.of(2025, 9, 19))
                .setDisplayOrder(3));

        createRoom("6-1", "6", 1, day);
        createRoom("18-1", "18", 2, day);
        createRoom("16-1", "16", 3, day);

        createTimeSlotsForDay(day, List.of("09:10", "18:15"), 0);
        createTimeSlotsForDay(day, List.of("09:10", "12:30"), 1);
        createTimeSlotsForDay(day, List.of("10:15", "13:45"), 2);
        createTimeSlotsForDay(day, List.of("14:45", "17:00"), 3);
        createTimeSlotsForDay(day, List.of("14:45", "17:00"), 4);
        createTimeSlotsForDay(day, List.of("14:45", "17:00"), 5);
    }

    private void createDayTwoWorkshops() {
        Day day = dayRepository.save(new Day()
                .setId("day-2-workshops")
                .setLabel("Day 2 Workshops")
                .setDate(LocalDate.of(2025, 9, 20))
                .setDisplayOrder(4));

        createRoom("6-2", "Room 6", 1, day);
        createRoom("18-2", "Room 18", 2, day);
        createRoom("16-2", "Room 16", 3, day);
        createRoom("4-2", "Room 4", 4, day);

        createTimeSlotsForDay(day, List.of("09:10", "12:30"), 0);
        createTimeSlotsForDay(day, List.of("14:45", "18:15"), 1);
        createTimeSlotsForDay(day, List.of("11:30", "13:45"), 2);
        createTimeSlotsForDay(day, List.of("09:10", "12:30"), 3);
        createTimeSlotsForDay(day, List.of("12:45", "15:45"), 4);
        createTimeSlotsForDay(day, List.of("16:00", "18:15"), 5);
    }

    private void createDayTwo() {
        Day day2 = new Day()
                .setId("day-2")
                .setLabel("Day 2")
                .setDate(LocalDate.of(2025, 9, 20))
                .setDisplayOrder(2);

        day2 = dayRepository.save(day2);

        createRoom("ab-2", "AB", 1, day2);
        createRoom("cde-2", "CDE", 2, day2);
        createRoom("13-2", "13", 3, day2);

        createTimeSlotsForDay(day2, List.of(
                "08:00", "09:00", "09:10", "10:00",
                "10:15", "11:15", "11:30", "12:30",
                "12:45", "13:45", "14:45",
                "15:45", "16:00", "17:00", "17:15",
                "18:10"
        ), 0);
    }

    private void createDayOne() {
        Day day1 = dayRepository.save(new Day()
                .setId("day-1")
                .setLabel("Day 1")
                .setDate(LocalDate.of(2025, 9, 19))
                .setDisplayOrder(1));

        createRoom("ab-1", "AB", 1, day1);
        createRoom("cde-1", "CDE", 2, day1);
        createRoom("13-1", "13", 3, day1);

        createTimeSlotsForDay(day1, List.of(
                "08:00", "09:00", "09:10", "10:00",
                "10:15", "10:45", "11:15", "11:30",
                "12:00", "12:30", "12:45", "13:15",
                "13:45", "14:45", "15:15",
                "15:45", "16:00", "16:30", "17:00",
                "17:15", "17:45", "18:15"
        ), 0);
    }

    private void createTimeSlotsForDay(Day day, List<String> times, int n) {
        List<StringTimeSlot> schedule = StringTimeSlot.createFromTimes(times);

        for (int i = 0; i < schedule.size(); i++) {
            var timeSlot = schedule.get(i);
            TimeSlot slot = new TimeSlot()
                    .setStart(timeSlot.getStart())
                    .setEnd(timeSlot.getEnd())
                    .setId(getTimeSlotId(day, i + n));
            timeSlotsRepository.save(slot);
        }
    }

    @NotNull
    private static TimeSlotId getTimeSlotId(Day day, int displayOrder) {
        return new TimeSlotId(day.getId(), displayOrder);
    }

    private void createRoom(String id, String label, int displayOrder, Day day) {
        roomRepository.save(new Room()
                .setId(id)
                .setLabel(label)
                .setDisplayOrder(displayOrder)
                .setDay(day));
    }

    private static void loginAsAdmin() {
        JelatynaPrincipal principal = new JelatynaPrincipal().setId("system").setName("system").setAdmin(true);
        var token = new PreAuthenticatedAuthenticationToken(principal, "", emptyList());
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}

record StringTimeSlot(String start, String end) {


    public static List<StringTimeSlot> createFromTimes(List<String> times) {
        List<StringTimeSlot> slots = new ArrayList<>();
        for (int i = 0; i < times.size() - 1; i++) {
            slots.add(new StringTimeSlot(times.get(i), times.get(i + 1)));
        }
        return slots;
    }

    public LocalTime getStart() {
        return LocalTime.parse(start);
    }

    public LocalTime getEnd() {
        return LocalTime.parse(end);
    }
}
