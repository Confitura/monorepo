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

        // Create test time slots: two for day1 and one for day2
        TimeSlot day1Slot1 = new TimeSlot()
                .setStart(LocalTime.of(9, 0))
                .setEnd(LocalTime.of(10, 0))
                .setId(new TimeSlot.TimeSlotId(day1.getId(), 1));

        TimeSlot day1Slot2 = new TimeSlot()
                .setStart(LocalTime.of(10, 15))
                .setEnd(LocalTime.of(11, 15))
                .setId(new TimeSlot.TimeSlotId(day1.getId(), 2));

        TimeSlot day2Slot1 = new TimeSlot()
                .setStart(LocalTime.of(9, 0))
                .setEnd(LocalTime.of(10, 0))
                .setId(new TimeSlot.TimeSlotId(day2.getId(), 1));

        day1Slot1 = timeSlotsRepository.save(day1Slot1);
        day1Slot2 = timeSlotsRepository.save(day1Slot2);
        day2Slot1 = timeSlotsRepository.save(day2Slot1);

        // Expose slots we need in other tests
        timeSlot1 = day1Slot1;
        timeSlot2 = day1Slot2;

        // Create test agenda entries: one per day
        entry1 = new AgendaEntry()
                .setTimeSlot(day1Slot1)
                .setRoom(room1)
                .setLabel("Opening Keynote");

        entry2 = new AgendaEntry()
                .setTimeSlot(day2Slot1)
                .setRoom(room1)
                .setLabel("Closing Keynote");

        entry1 = agendaRepository.save(entry1);
        entry2 = agendaRepository.save(entry2);
    }

    @Test
    void shouldReturnAllAgendaEntries() throws Exception {
        mockMvc.perform(get("/agenda/" + day1.getId() + "/entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/agenda/" + day2.getId() + "/entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnAgendaEntriesByDay() throws Exception {
        mockMvc.perform(get("/agenda/" + day1.getId() + "/entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].label", is("Opening Keynote")));

        mockMvc.perform(get("/agenda/" + day2.getId() + "/entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].label", is("Closing Keynote")));
    }

    @Test
    void shouldReturnAgendaEntryById() throws Exception {
        mockMvc.perform(get("/agenda/entries/" + entry1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("Opening Keynote")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateAgendaEntry() throws Exception {
        String entryJson = String.format(
                "{\"dayId\":\"%s\",\"timeSlotIndex\":%d,\"roomId\":\"%s\",\"label\":\"Workshop\",\"presentationId\":null}",
                day1.getId(), timeSlot2.getId().displayOrder(), room1.getId());

        mockMvc.perform(post("/agenda/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.label", is("Workshop")));

        // Verify that the entry was added to day1
        mockMvc.perform(get("/agenda/" + day1.getId() + "/entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteAgendaEntry() throws Exception {
        mockMvc.perform(delete("/agenda/entries/" + entry1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/agenda/" + day1.getId() + "/entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReplaceAgendaEntryContent() throws Exception {
        mockMvc.perform(put("/agenda/entries/" + entry1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Lunch\",\"roomId\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("Lunch")))
                .andExpect(jsonPath("$.roomLabel").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldMoveAgendaEntryToAnotherTimeSlot() throws Exception {
        String request = String.format("{\"dayId\":\"%s\",\"timeSlotIndex\":%d,\"roomId\":\"%s\"}",
                day1.getId(), timeSlot2.getDisplayOrder(), room1.getId());

        mockMvc.perform(put("/agenda/entries/" + entry1.getId() + "/slot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeSlotIndex", is(timeSlot2.getDisplayOrder())))
                .andExpect(jsonPath("$.timeSlotLabel", is("10:15 - 11:15")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateTimeSlotAsTheLastOneOfTheDay() throws Exception {
        mockMvc.perform(post("/agenda/" + day1.getId() + "/time-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"11:30\",\"end\":\"12:30\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayOrder", is(3)))
                .andExpect(jsonPath("$.label", is("11:30 - 12:30")))
                .andExpect(jsonPath("$.forAllRooms", is(false)));

        mockMvc.perform(get("/agenda/" + day1.getId() + "/time-slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnTimeSlotsInChronologicalOrder() throws Exception {
        // added as the last one, but starting before all the other slots
        mockMvc.perform(post("/agenda/" + day1.getId() + "/time-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"08:00\",\"end\":\"09:00\",\"forAllRooms\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayOrder", is(3)))
                .andExpect(jsonPath("$.forAllRooms", is(true)));

        mockMvc.perform(get("/agenda/" + day1.getId() + "/time-slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].start", is("08:00")))
                .andExpect(jsonPath("$[1].start", is("09:00")))
                .andExpect(jsonPath("$[2].start", is("10:15")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectTimeSlotWithUnparsableTime() throws Exception {
        mockMvc.perform(post("/agenda/" + day1.getId() + "/time-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"noon\",\"end\":\"12:30\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotCreateTimeSlotForUnknownDay() throws Exception {
        mockMvc.perform(post("/agenda/non-existent-id/time-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"11:30\",\"end\":\"12:30\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateTimeSlotTimes() throws Exception {
        mockMvc.perform(put("/agenda/" + day1.getId() + "/time-slots/" + timeSlot1.getDisplayOrder())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"09:15\",\"end\":\"10:05\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("09:15 - 10:05")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteTimeSlotWithItsEntries() throws Exception {
        mockMvc.perform(delete("/agenda/" + day1.getId() + "/time-slots/" + timeSlot1.getDisplayOrder()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/agenda/" + day1.getId() + "/time-slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/agenda/" + day1.getId() + "/entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotDeleteUnknownTimeSlot() throws Exception {
        mockMvc.perform(delete("/agenda/" + day1.getId() + "/time-slots/42"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateRoomAsTheLastOneOfTheDay() throws Exception {
        mockMvc.perform(post("/agenda/" + day1.getId() + "/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Side Room\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.label", is("Side Room")))
                .andExpect(jsonPath("$.displayOrder", is(room1.getDisplayOrder() + 1)));

        mockMvc.perform(get("/agenda/" + day1.getId() + "/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateRoomLabelAndDisplayOrder() throws Exception {
        mockMvc.perform(put("/agenda/rooms/" + room1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Big Hall\",\"displayOrder\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("Big Hall")))
                .andExpect(jsonPath("$.displayOrder", is(5)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRemoveRoomWithItsEntries() throws Exception {
        mockMvc.perform(delete("/agenda/rooms/" + room1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/agenda/" + day1.getId() + "/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/agenda/" + day1.getId() + "/entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotRemoveUnknownRoom() throws Exception {
        mockMvc.perform(delete("/agenda/rooms/non-existent-id"))
                .andExpect(status().isNotFound());
    }
}