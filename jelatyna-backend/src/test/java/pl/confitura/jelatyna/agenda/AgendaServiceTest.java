package pl.confitura.jelatyna.agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    @Mock
    private DayRepository dayRepository;

    @Mock
    private TimeSlotsRepository timeSlotsRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PresentationRepository presentationRepository;

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private AgendaService agendaService;

    // Given
    private final int timeSlotIndex = 0;
    private final String dayId = "day1";
    private final String roomId = "room1";
    private final String label = "Test Session";
    private final String presentationId = "presentation1";

    private TimeSlot timeSlot;
    private Room room;
    private Presentation presentation;

    @BeforeEach
    void setUp() {
        var day = new Day()
                .setId("day1")
                .setLabel("Day 1")
                .setDate(LocalDate.of(2025, 9, 1))
                .setDisplayOrder(1);

        timeSlot = new TimeSlot()
                .setStart(LocalTime.of(9, 0))
                .setEnd(LocalTime.of(10, 0))
                .setId(new TimeSlot.TimeSlotId("day-1", 1));

        room = new Room()
                .setId("room1")
                .setLabel("Main Hall")
                .setDisplayOrder(1)
                .setDay(day);

        presentation = new Presentation();
        presentation.setId("presentation1");

        lenient().when(dayRepository.findById(dayId)).thenReturn(day);
        lenient().when(timeSlotsRepository.findById(new TimeSlot.TimeSlotId(dayId, timeSlotIndex))).thenReturn(timeSlot);
        lenient().when(roomRepository.findById(roomId)).thenReturn(room);
        lenient().when(presentationRepository.findById(presentationId)).thenReturn(presentation);
    }

    @Test
    void shouldCreateAgendaEntryWithAllFields() {

        // When
        AgendaEntry result = agendaService.createAgendaEntry(dayId, timeSlotIndex, roomId, label, presentationId);

        // Then
        assertEquals(timeSlot, result.getTimeSlot());
        assertEquals(room, result.getRoom());
        assertEquals(label, result.getLabel());
        assertEquals(presentation, result.getPresentation());
    }

    @Test
    void shouldCreateAgendaEntryWithoutRoom() {

        // When
        AgendaEntry result = agendaService.createAgendaEntry(dayId, timeSlotIndex, null, label, presentationId);

        // Then
        assertEquals(timeSlot, result.getTimeSlot());
        assertNull(result.getRoom());
        assertEquals(label, result.getLabel());
        assertEquals(presentation, result.getPresentation());
    }

    @Test
    void shouldCreateAgendaEntryWithoutPresentation() {

        // When
        AgendaEntry result = agendaService.createAgendaEntry(dayId, timeSlotIndex, roomId, label, null);

        // Then
        assertEquals(timeSlot, result.getTimeSlot());
        assertEquals(room, result.getRoom());
        assertEquals(label, result.getLabel());
        assertNull(result.getPresentation());
    }

    @Test
    void inlineEntriesForMultipleTimeSlots() {
        var ts1 = new TimeSlot()
                .setStart(LocalTime.of(9, 0))
                .setEnd(LocalTime.of(10, 0))
                .setId(new TimeSlot.TimeSlotId("day-1", 1));
        var ts2 = new TimeSlot()
                .setStart(LocalTime.of(10, 0))
                .setEnd(LocalTime.of(11, 0))
                .setId(new TimeSlot.TimeSlotId("day-1", 2));
        AgendaEntry a1 = new AgendaEntry().setTimeSlot(ts1).setRoom(room).setPresentation(presentation);
        AgendaEntry a2 = new AgendaEntry().setTimeSlot(ts2).setRoom(room).setPresentation(presentation);
        when(agendaRepository.findAll()).thenReturn(List.of(a1, a2));

        var merged = agendaService.findAllAndMerge();
        assertThat(merged.size()).isEqualTo(1);

        TimeSlot mergedSlot = merged.getFirst().getTimeSlot();
        assertThat(mergedSlot.getStart()).isEqualTo(LocalTime.of(9, 0));
        assertThat(mergedSlot.getEnd()).isEqualTo(LocalTime.of(11, 0));
        assertThat(mergedSlot.getId().displayOrder()).isEqualTo(1);
    }

    @Test
    void notMergeLabels() {
        var ts1 = new TimeSlot()
                .setStart(LocalTime.of(9, 0))
                .setEnd(LocalTime.of(10, 0))
                .setId(new TimeSlot.TimeSlotId("day-1", 1));
        var ts2 = new TimeSlot()
                .setStart(LocalTime.of(10, 0))
                .setEnd(LocalTime.of(11, 0))
                .setId(new TimeSlot.TimeSlotId("day-1", 2));
        AgendaEntry a1 = new AgendaEntry().setTimeSlot(ts1).setRoom(room).setLabel("break");
        AgendaEntry a2 = new AgendaEntry().setTimeSlot(ts2).setRoom(room).setLabel("break");
        when(agendaRepository.findAll()).thenReturn(List.of(a1, a2));

        var merged = agendaService.findAllAndMerge();
        assertThat(merged.size()).isEqualTo(2);
    }
}