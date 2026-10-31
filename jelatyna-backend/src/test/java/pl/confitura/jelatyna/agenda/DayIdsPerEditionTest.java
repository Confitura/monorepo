package pl.confitura.jelatyna.agenda;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.edition.Edition;
import pl.confitura.jelatyna.edition.EditionRepository;
import pl.confitura.jelatyna.webpage.archive.domain.WebpageDataDumper;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;

// A Day id is unique within its Edition (ADR-0004): the webpage's schedule pages hardcode the Day
// ids, so every Edition uses the same ones.
@Transactional
class DayIdsPerEditionTest extends BaseIntegrationTest {

    private static final String DAY_ID = "shared-day";

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
    @Autowired
    WebpageDataDumper webpageDataDumper;

    private Day pastDay;
    private AgendaEntry pastEntry;

    @BeforeEach
    void scheduleADayWithTheSameIdInAPastAndTheCurrentEdition() {
        SecurityHelper.asAdmin();
        Edition past = editionRepository.save(new Edition().setYear(2025).setName("Confitura 2025")
                .setConferenceStart(Instant.parse("2025-07-04T07:00:00Z")).setLastDay(LocalDate.parse("2025-07-05")));
        pastDay = day(past, "2025-07-04");
        pastEntry = scheduled(pastDay, "Last Year's");
        timeSlotsRepository.save(new TimeSlot().setId(pastDay, 1).setStart(LocalTime.of(10, 0)).setEnd(LocalTime.of(11, 0)));
        scheduled(day(editionRepository.findCurrent(), "2026-09-25"), "This Year's");
        SecurityHelper.cleanSecurity();
    }

    @Test
    void theAdminAgendaShowsTheCurrentEditionsDay() throws Exception {
        mockMvc.perform(get("/days/" + DAY_ID).with(admin())).andExpect(jsonPath("$.date", is("2026-09-25")));
        mockMvc.perform(get("/agenda/" + DAY_ID + "/rooms").with(admin()))
                .andExpect(jsonPath("$[*].label", contains("This Year's Room")));
        mockMvc.perform(get("/agenda/" + DAY_ID + "/time-slots").with(admin())).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/agenda/" + DAY_ID + "/entries").with(admin()))
                .andExpect(jsonPath("$[*].label", contains("This Year's Keynote")));
        mockMvc.perform(get("/agenda/entries/" + pastEntry.getId()).with(admin())).andExpect(status().isNotFound());
    }

    @Test
    void changingTheCurrentEditionsDayLeavesThePastEditionsDayAsItWas() throws Exception {
        mockMvc.perform(put("/days/" + DAY_ID).with(admin()).contentType(APPLICATION_JSON)
                        .content("""
                                {"label":"Friday","date":"2026-09-18","displayOrder":7}"""))
                .andExpect(status().isOk());
        // The past Edition's Day has time slots 0 and 1, the current Edition's only 0.
        mockMvc.perform(post("/agenda/" + DAY_ID + "/time-slots").with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"start\":\"10:00\",\"end\":\"11:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayOrder", is(1)));
        mockMvc.perform(put("/agenda/" + DAY_ID + "/time-slots/0").with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"start\":\"08:30\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/agenda/" + DAY_ID + "/rooms").with(admin()).contentType(APPLICATION_JSON)
                        .content("{\"label\":\"Side Room\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(delete("/agenda/" + DAY_ID + "/time-slots/0").with(admin())).andExpect(status().isNoContent());

        assertThePastEditionsDayIsAsItWas();
    }

    @Test
    void aDayCanBeCreatedWithAnIdAPastEditionUses() throws Exception {
        mockMvc.perform(delete("/days/" + DAY_ID).with(admin())).andExpect(status().isNoContent());

        mockMvc.perform(post("/days").with(admin()).contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"shared-day","label":"Friday","date":"2026-09-18","displayOrder":1}"""))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/agenda/" + DAY_ID + "/rooms").with(admin())).andExpect(jsonPath("$", hasSize(0)));
        assertThePastEditionsDayIsAsItWas();
    }

    @Test
    void theDumpedDayIsTheCurrentEditionsDay(@TempDir Path folder) throws Exception {
        webpageDataDumper.dumpAgendaByDay(editionRepository.findCurrent(), folder.toString());

        String day = Files.readString(folder.resolve("agenda/" + DAY_ID + ".json"));
        assertThat(JsonPath.<List<String>>read(day, "$.rooms[*].label")).containsExactly("This Year's Room");
        assertThat(JsonPath.<List<Object>>read(day, "$.timeSlots")).hasSize(1);
        assertThat(JsonPath.<List<String>>read(day, "$.agendaEntries[*].label")).containsExactly("This Year's Keynote");
    }

    @Test
    void theCalendarDatesEachEntryOnItsOwnEditionsDay() throws Exception {
        String calendar = mockMvc.perform(get("/agenda/ical")).andReturn().getResponse().getContentAsString();

        assertThat(event(calendar, "Last Year's Keynote")).contains("DTSTART;TZID=Europe/Warsaw:20250704T090000");
        assertThat(event(calendar, "This Year's Keynote")).contains("DTSTART;TZID=Europe/Warsaw:20260925T090000");
    }

    private void assertThePastEditionsDayIsAsItWas() {
        Day day = dayRepository.findById(new Day.DayId(DAY_ID, pastDay.getEdition().getId()));
        assertThat(day.getLabel()).isEqualTo("Confitura 2025");
        assertThat(day.getDate()).isEqualTo("2025-07-04");
        assertThat(roomRepository.findByDay(day)).extracting(Room::getLabel).containsExactly("Last Year's Room");
        assertThat(timeSlotsRepository.findByDay(day)).extracting(TimeSlot::getLabel)
                .containsExactlyInAnyOrder("09:00 - 10:00", "10:00 - 11:00");
        assertThat(agendaRepository.findByDay(day)).extracting(AgendaEntry::getLabel).containsExactly("Last Year's Keynote");
    }

    private Day day(Edition edition, String date) {
        return dayRepository.save(new Day().setId(DAY_ID).setLabel(edition.getName())
                .setDate(LocalDate.parse(date)).setDisplayOrder(1).setEdition(edition));
    }

    // A room and a 09:00 time slot of the Day, and an entry scheduled in them.
    private AgendaEntry scheduled(Day day, String year) {
        Room room = roomRepository.save(new Room().setId(DAY_ID + "-" + day.getEdition().getYear())
                .setLabel(year + " Room").setDisplayOrder(1).setDay(day));
        TimeSlot slot = timeSlotsRepository.save(new TimeSlot().setId(day, 0)
                .setStart(LocalTime.of(9, 0)).setEnd(LocalTime.of(10, 0)));
        return agendaRepository.save(new AgendaEntry().setTimeSlot(slot).setRoom(room).setLabel(year + " Keynote"));
    }

    private static String event(String calendar, String summary) {
        return Arrays.stream(calendar.split("BEGIN:VEVENT"))
                .filter(event -> event.contains("SUMMARY:" + summary))
                .findFirst().orElseThrow();
    }
}
