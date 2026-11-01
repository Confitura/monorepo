package pl.confitura.jelatyna.conference.agenda.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.agenda.domain.*;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.identity.user.web.SecurityHelper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.identity.user.web.SecurityHelper.admin;

// The admin agenda shows and edits only the Current Edition's Days (ADR-0004).
@Transactional
class CurrentEditionAgendaTest extends BaseIntegrationTest {

    @Autowired
    EditionRepository editionRepository;
    @Autowired
    DayRepository dayRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    TimeSlotsRepository timeSlotsRepository;
    @Autowired
    AgendaRepository agendaRepository;

    private AgendaEntry currentEntry;
    private AgendaEntry pastEntry;

    @BeforeEach
    void createAnAgendaDayInTheCurrentAndAPastEdition() {
        SecurityHelper.asAdmin();
        Edition past = editionRepository.save(new Edition().setYear(2025).setName("Confitura 2025")
                .setConferenceStart(Instant.parse("2025-07-05T07:00:00Z")).setLastDay(LocalDate.parse("2025-07-05")));
        currentEntry = scheduledDay("current-day", editionRepository.findCurrent());
        pastEntry = scheduledDay("past-day", past);
        SecurityHelper.cleanSecurity();
    }

    @Test
    void adminSeesOnlyTheCurrentEditionsDays() throws Exception {
        mockMvc.perform(get("/days").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem("current-day")))
                .andExpect(jsonPath("$[*].id", not(hasItem("past-day"))));
        mockMvc.perform(get("/agenda/days").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem("current-day")))
                .andExpect(jsonPath("$[*].id", not(hasItem("past-day"))));
    }

    @Test
    void aPastEditionsDayAndItsAgendaAreNotFound() throws Exception {
        mockMvc.perform(get("/days/past-day").with(admin())).andExpect(status().isNotFound());
        mockMvc.perform(get("/agenda/past-day/entries").with(admin())).andExpect(status().isNotFound());
        mockMvc.perform(get("/agenda/entries/" + pastEntry.getId()).with(admin())).andExpect(status().isNotFound());
        mockMvc.perform(get("/agenda/past-day/rooms").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        mockMvc.perform(get("/agenda/past-day/time-slots").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void aPastEditionsDayCannotBeChanged() throws Exception {
        mockMvc.perform(put("/days/past-day").with(admin()).contentType(APPLICATION_JSON)
                        .content("""
                                {"label":"Friday","date":"2026-09-18","displayOrder":7}"""))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/days/past-day").with(admin())).andExpect(status().isNotFound());

        Day pastDay = pastEntry.getRoom().getDay();
        assertThat(pastDay.getLabel()).isEqualTo("past-day");
        assertThat(pastDay.getEdition().isCurrent()).isFalse();
    }

    @Test
    void aPastEditionsRoomsAndTimeSlotsCannotBeChanged() throws Exception {
        mockMvc.perform(post("/agenda/past-day/rooms").with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"label\":\"Side Room\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/agenda/rooms/past-day-room").with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"label\":\"Big Hall\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/agenda/rooms/past-day-room").with(admin())).andExpect(status().isNotFound());
        mockMvc.perform(post("/agenda/past-day/time-slots").with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"start\":\"11:30\",\"end\":\"12:30\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/agenda/past-day/time-slots/0").with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"start\":\"09:15\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/agenda/past-day/time-slots/0").with(admin())).andExpect(status().isNotFound());

        assertThat(roomRepository.findByDay(pastEntry.getRoom().getDay())).extracting(Room::getLabel).containsExactly("Main Hall");
        assertThat(timeSlotsRepository.findByDay(pastEntry.getRoom().getDay())).extracting(TimeSlot::getLabel).containsExactly("09:00 - 10:00");
    }

    @Test
    void aPastEditionsEntriesCannotBeChanged() throws Exception {
        mockMvc.perform(post("/agenda/entries").with(admin()).contentType(APPLICATION_JSON)
                        .content("""
                                {"dayId":"past-day","timeSlotIndex":0,"roomId":"past-day-room","label":"Lunch"}"""))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/agenda/entries/" + pastEntry.getId()).with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"label\":\"Lunch\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/agenda/entries/" + pastEntry.getId() + "/slot").with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"dayId\":\"current-day\",\"timeSlotIndex\":0}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/agenda/entries/" + pastEntry.getId()).with(admin())).andExpect(status().isNotFound());

        assertThat(agendaRepository.findByDay(pastEntry.getRoom().getDay()))
                .extracting(AgendaEntry::getId, AgendaEntry::getLabel)
                .containsExactly(tuple(pastEntry.getId(), "Keynote"));
    }

    @Test
    void theCurrentEditionsEntriesCannotReachAPastEditionsDay() throws Exception {
        mockMvc.perform(put("/agenda/entries/" + currentEntry.getId() + "/slot").with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"dayId\":\"past-day\",\"timeSlotIndex\":0}"))
                .andExpect(status().isNotFound());
        // A past Edition's room is unknown, and an unknown room leaves the entry without one.
        mockMvc.perform(put("/agenda/entries/" + currentEntry.getId()).with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"label\":\"Keynote\",\"roomId\":\"past-day-room\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").doesNotExist());
        mockMvc.perform(post("/agenda/entries").with(admin()).contentType(APPLICATION_JSON)
                        .content("""
                                {"dayId":"current-day","timeSlotIndex":0,"roomId":"past-day-room","label":"Lunch"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").doesNotExist());

        assertThat(agendaRepository.findByRoomId("past-day-room")).extracting(AgendaEntry::getId).containsExactly(pastEntry.getId());
    }

    @Test
    void theRoomsEndpointsShowAndChangeOnlyTheCurrentEditionsRooms() throws Exception {
        mockMvc.perform(get("/rooms").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem("current-day-room")))
                .andExpect(jsonPath("$[*].id", not(hasItem("past-day-room"))));
        mockMvc.perform(get("/rooms/past-day-room").with(admin())).andExpect(status().isNotFound());
        mockMvc.perform(post("/rooms").with(admin()).contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"new-room","label":"Side Room","displayOrder":2,"day":{"id":"past-day"}}"""))
                .andExpect(status().isNotFound());
        // Room ids are global, so saving a past Edition's room id would take that room over.
        mockMvc.perform(post("/rooms").with(admin()).contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"past-day-room","label":"Side Room","displayOrder":2,"day":{"id":"current-day"}}"""))
                .andExpect(status().isConflict());
        mockMvc.perform(delete("/rooms/past-day-room").with(admin())).andExpect(status().isNotFound());

        assertThat(roomRepository.findByDay(pastEntry.getRoom().getDay()))
                .extracting(Room::getId, Room::getLabel)
                .containsExactly(tuple("past-day-room", "Main Hall"));
    }

    // A Day with one room and one time slot, and an entry scheduled in them.
    private AgendaEntry scheduledDay(String dayId, Edition edition) {
        Day day = dayRepository.save(new Day().setId(dayId).setLabel(dayId)
                .setDate(LocalDate.parse("2025-07-05")).setDisplayOrder(1).setEdition(edition));
        Room room = roomRepository.save(new Room().setId(dayId + "-room").setLabel("Main Hall")
                .setDisplayOrder(1).setDay(day));
        TimeSlot slot = timeSlotsRepository.save(new TimeSlot().setId(day, 0)
                .setStart(LocalTime.of(9, 0)).setEnd(LocalTime.of(10, 0)));
        return agendaRepository.save(new AgendaEntry().setTimeSlot(slot).setRoom(room).setLabel("Keynote"));
    }
}
