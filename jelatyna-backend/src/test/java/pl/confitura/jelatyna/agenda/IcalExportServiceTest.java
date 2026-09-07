package pl.confitura.jelatyna.agenda;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.confitura.jelatyna.presentation.Presentation;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IcalExportServiceTest {

    @Mock
    AgendaService agendaService;
    @Mock
    DayRepository dayRepository;

    @InjectMocks
    IcalExportService service;

    @Test
    void generatesIcsWithEvents() {
        Day day = new Day().setId("day-1").setDate(LocalDate.of(2025, 9, 19)).setLabel("Day 1").setDisplayOrder(1);

        TimeSlot slotTalk = new TimeSlot()
                .setId("day-1", 1)
                .setStart(LocalTime.of(9, 10))
                .setEnd(LocalTime.of(10, 0));
        Room room = new Room().setId("ab-1").setLabel("AB").setDisplayOrder(1).setDay(day);
        Presentation pres = new Presentation().setTitle("Awesome Talk").setShortDescription("Short");
        AgendaEntry talk = new AgendaEntry().setId("1").setTimeSlot(slotTalk).setRoom(room).setPresentation(pres);

        TimeSlot slotGeneral = new TimeSlot()
                .setId("day-1", 0)
                .setStart(LocalTime.of(8, 0))
                .setEnd(LocalTime.of(9, 0));
        AgendaEntry general = new AgendaEntry().setId("2").setTimeSlot(slotGeneral).setLabel("Registration");

        when(agendaService.findAllAndMerge()).thenReturn(List.of(talk, general));
        when(dayRepository.findById("day-1")).thenReturn(day);

        byte[] ics = service.generateIcs();
        String txt = new String(ics, StandardCharsets.UTF_8);

        assertThat(txt).contains("BEGIN:VCALENDAR");
        assertThat(txt).contains("SUMMARY:Awesome Talk");
        assertThat(txt).contains("LOCATION:AB");
        assertThat(txt).contains("SUMMARY:Registration");
        assertThat(txt).contains("TZID=Europe/Warsaw");
    }

    @Test
    void mergesEntriesWithSameNameDateAndTime() {
        Day day = new Day().setId("day-1").setDate(LocalDate.of(2025, 9, 19)).setLabel("Day 1").setDisplayOrder(1);

        TimeSlot slot = new TimeSlot()
                .setId("day-1", 0)
                .setStart(LocalTime.of(10, 0))
                .setEnd(LocalTime.of(10, 30));

        Room roomA = new Room().setId("a").setLabel("Room A").setDisplayOrder(1).setDay(day);
        Room roomB = new Room().setId("b").setLabel("Room B").setDisplayOrder(2).setDay(day);
        AgendaEntry breakA = new AgendaEntry().setId("1").setTimeSlot(slot).setRoom(roomA).setLabel("Coffee Break");
        AgendaEntry breakB = new AgendaEntry().setId("2").setTimeSlot(slot).setRoom(roomB).setLabel("Coffee Break");

        when(agendaService.findAllAndMerge()).thenReturn(List.of(breakA, breakB));
        when(dayRepository.findById("day-1")).thenReturn(day);

        byte[] ics = service.generateIcs();
        String txt = new String(ics, StandardCharsets.UTF_8);

        assertThat(countOccurrences(txt, "BEGIN:VEVENT")).isEqualTo(1);
        assertThat(countOccurrences(txt, "SUMMARY:Coffee Break")).isEqualTo(1);
        assertThat(txt).contains("LOCATION:Room A\\, Room B");
    }

    private static int countOccurrences(String text, String token) {
        int count = 0;
        for (int i = text.indexOf(token); i >= 0; i = text.indexOf(token, i + token.length())) {
            count++;
        }
        return count;
    }
}
