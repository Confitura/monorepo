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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @InjectMocks
    private AgendaService agendaService;

    private Day day;
    private TimeSlot timeSlot;
    private Room room;
    private Presentation presentation;

    @BeforeEach
    void setUp() {
        // Create test data
        day = new Day()
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
    }

    @Test
    void shouldCreateAgendaEntryWithAllFields() {
        // Given
        String dayId = "day1";
        int timeSlotIndex = 0;
        String roomId = "room1";
        String label = "Test Session";
        String presentationId = "presentation1";

        when(dayRepository.findById(dayId)).thenReturn(day);
        when(timeSlotsRepository.findById(new TimeSlot.TimeSlotId(dayId, timeSlotIndex))).thenReturn(timeSlot);
        when(roomRepository.findById(roomId)).thenReturn(room);
        when(presentationRepository.findById(presentationId)).thenReturn(presentation);

        // When
        AgendaEntry result = agendaService.createAgendaEntry(dayId, timeSlotIndex, roomId, label, presentationId);

        // Then
        assertEquals(day, result.getDay());
        assertEquals(timeSlot, result.getTimeSlot());
        assertEquals(room, result.getRoom());
        assertEquals(label, result.getLabel());
        assertEquals(presentation, result.getPresentation());
    }

    @Test
    void shouldCreateAgendaEntryWithoutRoom() {
        // Given
        String dayId = "day1";
        int timeSlotIndex = 0;
        String label = "Test Session";
        String presentationId = "presentation1";

        when(dayRepository.findById(dayId)).thenReturn(day);
        when(timeSlotsRepository.findById(new TimeSlot.TimeSlotId(dayId, timeSlotIndex))).thenReturn(timeSlot);
        when(presentationRepository.findById(presentationId)).thenReturn(presentation);

        // When
        AgendaEntry result = agendaService.createAgendaEntry(dayId, timeSlotIndex, null, label, presentationId);

        // Then
        assertEquals(day, result.getDay());
        assertEquals(timeSlot, result.getTimeSlot());
        assertNull(result.getRoom());
        assertEquals(label, result.getLabel());
        assertEquals(presentation, result.getPresentation());
    }

    @Test
    void shouldCreateAgendaEntryWithoutPresentation() {
        // Given
        String dayId = "day1";
        int timeSlotIndex = 0;
        String roomId = "room1";
        String label = "Test Session";

        when(dayRepository.findById(dayId)).thenReturn(day);
        when(timeSlotsRepository.findById(new TimeSlot.TimeSlotId(dayId,timeSlotIndex ))).thenReturn(timeSlot);
        when(roomRepository.findById(roomId)).thenReturn(room);

        // When
        AgendaEntry result = agendaService.createAgendaEntry(dayId, timeSlotIndex, roomId, label, null);

        // Then
        assertEquals(day, result.getDay());
        assertEquals(timeSlot, result.getTimeSlot());
        assertEquals(room, result.getRoom());
        assertEquals(label, result.getLabel());
        assertNull(result.getPresentation());
    }
}