package pl.confitura.jelatyna.agenda;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.infrastructure.security.SecurityContextUtil;

import java.time.LocalDate;
import java.time.LocalTime;

import static java.util.Collections.emptyList;

@Configuration
public class AgendaInitializer {

    private final DayRepository dayRepository;
    private final RoomRepository roomRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final AgendaRepository agendaRepository;

    public AgendaInitializer(DayRepository dayRepository,
                             RoomRepository roomRepository,
                             TimeSlotsRepository timeSlotsRepository,
                             AgendaRepository agendaRepository) {
        this.dayRepository = dayRepository;
        this.roomRepository = roomRepository;
        this.timeSlotsRepository = timeSlotsRepository;
        this.agendaRepository = agendaRepository;
    }

    //    @Bean
//    @Profile("!test")
//    InitializingBean initAgenda(Security security) {
//        return () -> {
//            init();
//        };
//    }
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        // This code runs after all Spring initialization is complete
        // including SpEL, beans, security, etc.
        init();
    }

    private void init() {
        JelatynaPrincipal principal = new JelatynaPrincipal().setId("system").setName("system").setAdmin(true);
        var token = new PreAuthenticatedAuthenticationToken(principal, "", emptyList());
        SecurityContextHolder.getContext().setAuthentication(token);
        // Create two days
        Day day1 = new Day()
                .setLabel("Day 1")
                .setDate(LocalDate.of(2025, 8, 1))
                .setDisplayOrder(1);

        Day day2 = new Day()
                .setLabel("Day 2")
                .setDate(LocalDate.of(2025, 8, 2))
                .setDisplayOrder(2);

        day1 = dayRepository.save(day1);
        day2 = dayRepository.save(day2);

        // Create three rooms
        Room room1 = new Room()
                .setLabel("Main Hall")
                .setDisplayOrder(1);

        Room room2 = new Room()
                .setLabel("Workshop Room")
                .setDisplayOrder(2);

        Room room3 = new Room()
                .setLabel("Conference Room")
                .setDisplayOrder(3);

        room1 = roomRepository.save(room1);
        room2 = roomRepository.save(room2);
        room3 = roomRepository.save(room3);

        // Create time slots
        TimeSlot slot1 = new TimeSlot()
                .setStart(LocalTime.of(9, 0))
                .setEnd(LocalTime.of(10, 0))
                .setDisplayOrder(1);

        TimeSlot slot2 = new TimeSlot()
                .setStart(LocalTime.of(10, 15))
                .setEnd(LocalTime.of(11, 15))
                .setDisplayOrder(2);

        TimeSlot slot3 = new TimeSlot()
                .setStart(LocalTime.of(11, 30))
                .setEnd(LocalTime.of(12, 30))
                .setDisplayOrder(3);

        TimeSlot lunchSlot = new TimeSlot()
                .setStart(LocalTime.of(12, 30))
                .setEnd(LocalTime.of(13, 30))
                .setForAllRooms(true)
                .setDisplayOrder(4);

        TimeSlot slot4 = new TimeSlot()
                .setStart(LocalTime.of(13, 30))
                .setEnd(LocalTime.of(14, 30))
                .setDisplayOrder(5);

        TimeSlot slot5 = new TimeSlot()
                .setStart(LocalTime.of(14, 45))
                .setEnd(LocalTime.of(15, 45))
                .setDisplayOrder(6);

        TimeSlot slot6 = new TimeSlot()
                .setStart(LocalTime.of(16, 0))
                .setEnd(LocalTime.of(17, 0))
                .setDisplayOrder(7);

        slot1 = timeSlotsRepository.save(slot1);
        slot2 = timeSlotsRepository.save(slot2);
        slot3 = timeSlotsRepository.save(slot3);
        lunchSlot = timeSlotsRepository.save(lunchSlot);
        slot4 = timeSlotsRepository.save(slot4);
        slot5 = timeSlotsRepository.save(slot5);
        slot6 = timeSlotsRepository.save(slot6);

        // Create agenda entries for Day 1
        // Morning sessions
        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot1)
                .setRoom(room1)
                .setLabel("Opening Keynote"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot2)
                .setRoom(room1)
                .setLabel("Technical Session 1"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot2)
                .setRoom(room2)
                .setLabel("Workshop 1"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot2)
                .setRoom(room3)
                .setLabel("Panel Discussion 1"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot3)
                .setRoom(room1)
                .setLabel("Technical Session 2"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot3)
                .setRoom(room2)
                .setLabel("Workshop 2"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot3)
                .setRoom(room3)
                .setLabel("Panel Discussion 2"));

        // Lunch break
        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(lunchSlot)
                .setLabel("Lunch Break"));

        // Afternoon sessions
        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot4)
                .setRoom(room1)
                .setLabel("Technical Session 3"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot4)
                .setRoom(room2)
                .setLabel("Workshop 3"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot4)
                .setRoom(room3)
                .setLabel("Panel Discussion 3"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot5)
                .setRoom(room1)
                .setLabel("Technical Session 4"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot5)
                .setRoom(room2)
                .setLabel("Workshop 4"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot5)
                .setRoom(room3)
                .setLabel("Panel Discussion 4"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(slot6)
                .setRoom(room1)
                .setLabel("Day 1 Closing Remarks"));

        // Create agenda entries for Day 2
        // Morning sessions
        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot1)
                .setRoom(room1)
                .setLabel("Day 2 Keynote"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot2)
                .setRoom(room1)
                .setLabel("Technical Session 5"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot2)
                .setRoom(room2)
                .setLabel("Workshop 5"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot2)
                .setRoom(room3)
                .setLabel("Panel Discussion 5"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot3)
                .setRoom(room1)
                .setLabel("Technical Session 6"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot3)
                .setRoom(room2)
                .setLabel("Workshop 6"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot3)
                .setRoom(room3)
                .setLabel("Panel Discussion 6"));

        // Lunch break
        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(lunchSlot)
                .setLabel("Lunch Break"));

        // Afternoon sessions
        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot4)
                .setRoom(room1)
                .setLabel("Technical Session 7"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot4)
                .setRoom(room2)
                .setLabel("Workshop 7"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot4)
                .setRoom(room3)
                .setLabel("Panel Discussion 7"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot5)
                .setRoom(room1)
                .setLabel("Technical Session 8"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot5)
                .setRoom(room2)
                .setLabel("Workshop 8"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot5)
                .setRoom(room3)
                .setLabel("Panel Discussion 8"));

        agendaRepository.save(new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(slot6)
                .setRoom(room1)
                .setLabel("Conference Closing Remarks"));
    }
}
