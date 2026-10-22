package pl.confitura.jelatyna.agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.edition.Edition;
import pl.confitura.jelatyna.edition.EditionRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;

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
    void theCurrentEditionsDayAndItsAgendaAreShown() throws Exception {
        mockMvc.perform(get("/days/current-day").with(admin())).andExpect(status().isOk());
        mockMvc.perform(get("/agenda/current-day/entries").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/agenda/entries/" + currentEntry.getId()).with(admin())).andExpect(status().isOk());
        mockMvc.perform(get("/agenda/current-day/rooms").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/agenda/current-day/time-slots").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // A Day with one room and one time slot, and an entry scheduled in them.
    private AgendaEntry scheduledDay(String dayId, Edition edition) {
        Day day = dayRepository.save(new Day().setId(dayId).setLabel(dayId)
                .setDate(LocalDate.parse("2025-07-05")).setDisplayOrder(1).setEdition(edition));
        Room room = roomRepository.save(new Room().setId(dayId + "-room").setLabel("Main Hall")
                .setDisplayOrder(1).setDay(day));
        TimeSlot slot = timeSlotsRepository.save(new TimeSlot().setId(dayId, 0)
                .setStart(LocalTime.of(9, 0)).setEnd(LocalTime.of(10, 0)));
        return agendaRepository.save(new AgendaEntry().setTimeSlot(slot).setRoom(room).setLabel("Keynote"));
    }
}
