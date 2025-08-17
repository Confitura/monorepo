package pl.confitura.jelatyna.agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class AgendaServiceTest {

    private DayRepository dayRepository = Mockito.mock(DayRepository.class);

    private TimeSlotsRepository timeSlotsRepository = Mockito.mock(TimeSlotsRepository.class);

    private RoomRepository roomRepository = Mockito.mock(RoomRepository.class);

    private PresentationRepository presentationRepository = Mockito.mock(PresentationRepository.class);

    private AgendaService agendaService = new AgendaService(dayRepository, timeSlotsRepository, roomRepository, presentationRepository);

    // Given
    private int timeSlotIndex = 0;
    private String dayId = "day1";
    private String roomId = "room1";
    private String label = "Test Session";
    private String presentationId = "presentation1";

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


        when(dayRepository.findById(dayId)).thenReturn(day);
        when(timeSlotsRepository.findById(new TimeSlot.TimeSlotId(dayId, timeSlotIndex))).thenReturn(timeSlot);
        when(roomRepository.findById(roomId)).thenReturn(room);
        when(presentationRepository.findById(presentationId)).thenReturn(presentation);
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
}