package pl.confitura.jelatyna.agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.support.TransactionTemplate;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AgendaControllerTest extends BaseIntegrationTest {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private TimeSlotsRepository timeSlotsRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private TransactionTemplate txTemplate;

    private Day day1;
    private Day day2;
    private TimeSlot timeSlot1;
    private TimeSlot timeSlot2;
    private Room room1;
    private AgendaEntry entry1;
    private AgendaEntry entry2;

    @BeforeEach
    public void setUp() {
        SecurityHelper.asAdmin();
        
        // Perform all setup within a single transaction
        txTemplate.executeWithoutResult(status -> {
            // Clear existing data
            clearExistingData();
            
            // Create and save all entities within the same transaction
            createTestData();
        });
    }

    private void clearExistingData() {
        // Clear existing agenda entries
        Iterable<AgendaEntry> existingEntries = agendaRepository.findAll();
        for (AgendaEntry entry : existingEntries) {
            agendaRepository.deleteById(entry.getId());
        }

        var ts = timeSlotsRepository.findAll();
        for (TimeSlot timeSlot : ts) {
            timeSlotsRepository.deleteById(timeSlot.getId());
        }

        // Clear existing rooms
        var rooms = roomRepository.findAll();
        for (Room room : rooms) {
            roomRepository.deleteById(room.getId());
        }

        // Clear existing days
        Iterable<Day> existingDays = dayRepository.findAll();
        for (Day day : existingDays) {
            dayRepository.deleteById(day.getId());
        }
    }

    private void createTestData() {
        // Create test days
        day1 = new Day()
                .setId("day-1")
                .setLabel("Day 1")
                .setDate(LocalDate.of(2025, 9, 1))
                .setDisplayOrder(1);

        day2 = new Day()
                .setId("day-2")
                .setLabel("Day 2")
                .setDate(LocalDate.of(2025, 9, 2))
                .setDisplayOrder(2);

        day1 = dayRepository.save(day1);
        day2 = dayRepository.save(day2);

        // Create test room
        room1 = new Room()
                .setId("room-1")
                .setLabel("Main Hall")
                .setDisplayOrder(1)
                .setDay(day1); // Set the day relationship

        room1 = roomRepository.save(room1);

        // Create test time slots with proper day reference
        timeSlot1 = new TimeSlot()
                .setStart(LocalTime.of(9, 0))
                .setEnd(LocalTime.of(10, 0))
                .setId(new TimeSlot.TimeSlotId(day1.getId(), 1));

        timeSlot2 = new TimeSlot()
                .setStart(LocalTime.of(10, 15))
                .setEnd(LocalTime.of(11, 15))
                .setId(new TimeSlot.TimeSlotId(day1.getId(), 2));

        timeSlot1 = timeSlotsRepository.save(timeSlot1);
        timeSlot2 = timeSlotsRepository.save(timeSlot2);

        // Create test agenda entries with proper entity references
        entry1 = new AgendaEntry()
                .setDay(day1)
                .setTimeSlot(timeSlot1)
                .setRoom(room1)
                .setLabel("Opening Keynote");

        entry2 = new AgendaEntry()
                .setDay(day2)
                .setTimeSlot(timeSlot1)
                .setRoom(room1)
                .setLabel("Closing Keynote");

        entry1 = agendaRepository.save(entry1);
        entry2 = agendaRepository.save(entry2);
    }

    @Test
    void shouldReturnAllAgendaEntries() throws Exception {
        mockMvc.perform(get("/agenda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldReturnAgendaEntriesByDay() throws Exception {
        mockMvc.perform(get("/agenda/day/" + day1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].label", is("Opening Keynote")));

        mockMvc.perform(get("/agenda/day/" + day2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].label", is("Closing Keynote")));
    }

    @Test
    void shouldReturnAgendaEntryById() throws Exception {
        mockMvc.perform(get("/agenda/" + entry1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("Opening Keynote")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateAgendaEntry() throws Exception {
        String entryJson = String.format(
                "{\"dayId\":\"%s\",\"timeSlotId\":\"%s\",\"roomId\":\"%s\",\"label\":\"Workshop\"}",
                day1.getId(), timeSlot2.getId(), room1.getId());

        mockMvc.perform(post("/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.label", is("Workshop")));

        // Verify that the entry was added to day1
        mockMvc.perform(get("/agenda/day/" + day1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteAgendaEntry() throws Exception {
        mockMvc.perform(delete("/agenda/" + entry1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/agenda/day/" + day1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}