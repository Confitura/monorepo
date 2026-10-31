package pl.confitura.jelatyna.conference.edition.web;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.FileSystemUtils;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.Day;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.conference.voting.domain.VoterIpsCleaner;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.platform.storage.ResourceConfigurationProperties;
import pl.confitura.jelatyna.conference.voting.domain.Vote;
import pl.confitura.jelatyna.conference.voting.domain.VoteRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

class EditionControllerTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private ResourceConfigurationProperties resources;

    @Autowired
    private VoteRepository voteRepository;

    private Edition current;
    private Edition past;
    private Edition next;
    private Path nextFolder;
    // Newest first, so a vote goes before its talk.
    private final Deque<Object> created = new ArrayDeque<>();

    // The migration's Edition 2026 stays the Current Edition; every other one is the test's.
    @BeforeEach
    void editions() throws Exception {
        removeEveryEditionButTheCurrentOne();
        current = editionRepository.findCurrent();
        past = persist(edition(2025));
        next = persist(edition(2027).setC4pStart(Instant.parse("2027-02-01T00:00:00Z"))
                .setC4pEnd(Instant.parse("2027-05-01T00:00:00Z")));
        nextFolder = Path.of(resources.folder(), "edition-2027");
        FileSystemUtils.deleteRecursively(nextFolder);
    }

    // This test commits its Editions, and other tests create a past Edition 2025 of their own.
    void removeEveryEditionButTheCurrentOne() {
        txTemplate.executeWithoutResult(status -> {
            String notCurrent = "(select e from Edition e where e.current = false)";
            entityManager.createQuery("delete from Presentation p where p.edition in " + notCurrent).executeUpdate();
            entityManager.createQuery("delete from Day d where d.edition in " + notCurrent).executeUpdate();
            entityManager.createQuery("delete from Edition e where e.current = false").executeUpdate();
        });
    }

    // Other tests rely on the Current Edition and its C4P period, which a test here changes. It is made current
    // again before the other Editions go, as those are the non-current ones.
    @AfterEach
    void restoreTheCurrentEditionAndRemoveTheRest() throws Exception {
        txTemplate.executeWithoutResult(status -> {
            // At most one Edition can be current, so the one a test made current stops being it first. This
            // goes before anything loads an Edition, which would hide the update from the merge.
            entityManager.createQuery("update Edition e set e.current = false").executeUpdate();
            entityManager.merge(current);
            created.forEach(entity -> entityManager.remove(entityManager.merge(entity)));
        });
        removeEveryEditionButTheCurrentOne();
        FileSystemUtils.deleteRecursively(nextFolder);
    }

    @Test
    void makesAnEditionCurrent() throws Exception {
        makeCurrent(next)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(next.getId())))
                .andExpect(jsonPath("$.current", is(true)));

        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(jsonPath("$[*].year", contains(2027, 2026, 2025)))
                .andExpect(jsonPath("$[*].current", contains(true, false, false)));
    }

    @Test
    void clearsTheVoterIpsOfThePreviousCurrentEditionOnlyAndKeepsItsVotes() throws Exception {
        Vote thisYears = vote(current, "203.0.113.7");
        Vote lastYears = vote(past, "198.51.100.1");

        makeCurrent(next).andExpect(status().isOk());

        assertThat(voteRepository.findById(thisYears.getId())).extracting(Vote::getClient).isNull();
        assertThat(voteRepository.findById(lastYears.getId())).extracting(Vote::getClient).isEqualTo("198.51.100.1");
    }

    @Test
    void switchesBackToAnEarlierEditionWithoutRestoringItsVoterIps() throws Exception {
        Vote thisYears = vote(current, "203.0.113.7");
        Vote nextYears = vote(next, "192.0.2.10");
        makeCurrent(next).andExpect(status().isOk());

        makeCurrent(current).andExpect(status().isOk());

        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(jsonPath("$[*].current", contains(false, true, false)));
        assertThat(voteRepository.findById(thisYears.getId())).extracting(Vote::getClient).isNull();
        assertThat(voteRepository.findById(nextYears.getId())).extracting(Vote::getClient).isNull();
    }

    @Test
    void eachSwitchLogsTheIdsAndHowManyVotesLostTheirIp() throws Exception {
        vote(current, "203.0.113.7");
        vote(current, "203.0.113.8");
        Logger switches = (Logger) LoggerFactory.getLogger(EditionController.class);
        Logger clearings = (Logger) LoggerFactory.getLogger(VoterIpsCleaner.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        switches.addAppender(appender);
        clearings.addAppender(appender);

        try {
            makeCurrent(next).andExpect(status().isOk());
        } finally {
            switches.detachAppender(appender);
            clearings.detachAppender(appender);
        }

        assertThat(appender.list).extracting(event -> event.getKeyValuePairs().stream()
                        .collect(Collectors.toMap(pair -> pair.key, pair -> pair.value)))
                .containsExactlyInAnyOrder(
                        Map.of("editionId", current.getId(), "votesCleared", 2),
                        Map.of("actorUserId", "ADMIN", "previousEditionId", current.getId(), "editionId", next.getId()));
        assertThat(appender.list).allSatisfy(event -> assertThat(event.getFormattedMessage()).doesNotContain("203.0.113"));
    }

    @Test
    void dumpsIntoTheNewEditionsFolderStraightAway() throws Exception {
        makeCurrent(next).andExpect(status().isOk());

        Integer dumpedYear = JsonPath.read(Files.readString(nextFolder.resolve("edition.json")), "$.year");
        assertThat(dumpedYear).isEqualTo(2027);
    }

    @Test
    void aFailedDumpKeepsTheSwitch() throws Exception {
        // A file where the new Edition's folder belongs makes the dump fail.
        Files.createFile(nextFolder);

        makeCurrent(next).andExpect(status().isOk());

        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(jsonPath("$[*].current", contains(true, false, false)));
    }

    @Test
    void refusesToMakeTheCurrentEditionCurrentAgainOrAnUnknownOne() throws Exception {
        makeCurrent(current).andExpect(status().isConflict());
        mockMvc.perform(post("/editions/does-not-exist/make-current").with(admin()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(jsonPath("$[*].current", contains(false, true, false)));
    }

    @Test
    void listsEveryEditionNewestYearFirstMarkingTheCurrentOne() throws Exception {
        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].year", contains(2027, 2026, 2025)))
                .andExpect(jsonPath("$[*].current", contains(false, true, false)))
                .andExpect(jsonPath("$[0].id", is(next.getId())))
                .andExpect(jsonPath("$[0].name", is("Confitura 2027")))
                .andExpect(jsonPath("$[0].conferenceStart", is("2027-09-22T07:00:00Z")))
                .andExpect(jsonPath("$[0].lastDay", is("2027-09-23")))
                .andExpect(jsonPath("$[0].c4pStart", is("2027-02-01T00:00:00Z")))
                .andExpect(jsonPath("$[0].c4pEnd", is("2027-05-01T00:00:00Z")))
                .andExpect(jsonPath("$[2].c4pStart", nullValue()));
    }

    @Test
    void getsOneEdition() throws Exception {
        mockMvc.perform(get("/editions/" + past.getId()).with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year", is(2025)))
                .andExpect(jsonPath("$.name", is("Confitura 2025")));
        mockMvc.perform(get("/editions/does-not-exist").with(admin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsAnEditionThatIsNotCurrent() throws Exception {
        create(new EditionRequest(2028, "Confitura 2028", Instant.parse("2028-09-22T07:00:00Z"),
                LocalDate.parse("2028-09-23"), Instant.parse("2028-02-01T00:00:00Z"), Instant.parse("2028-05-01T00:00:00Z")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.year", is(2028)))
                .andExpect(jsonPath("$.name", is("Confitura 2028")))
                .andExpect(jsonPath("$.conferenceStart", is("2028-09-22T07:00:00Z")))
                .andExpect(jsonPath("$.lastDay", is("2028-09-23")))
                .andExpect(jsonPath("$.c4pStart", is("2028-02-01T00:00:00Z")))
                .andExpect(jsonPath("$.c4pEnd", is("2028-05-01T00:00:00Z")))
                .andExpect(jsonPath("$.current", is(false)));

        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(jsonPath("$[*].year", contains(2028, 2027, 2026, 2025)))
                .andExpect(jsonPath("$[*].current", contains(false, false, true, false)));
    }

    @Test
    void refusesAnEditionWithoutYearNameConferenceStartOrLastDay() throws Exception {
        Instant start = Instant.parse("2028-09-22T07:00:00Z");
        LocalDate lastDay = LocalDate.parse("2028-09-23");
        create(new EditionRequest(null, "Confitura", start, lastDay, null, null)).andExpect(status().isBadRequest());
        create(new EditionRequest(2028, null, start, lastDay, null, null)).andExpect(status().isBadRequest());
        create(new EditionRequest(2028, "Confitura", null, lastDay, null, null)).andExpect(status().isBadRequest());
        create(new EditionRequest(2028, "Confitura", start, null, null, null)).andExpect(status().isBadRequest());
    }

    @Test
    void refusesAC4pEndNotAfterItsStartOrALastDayBeforeTheConferenceStart() throws Exception {
        Instant start = Instant.parse("2028-09-22T07:00:00Z");
        LocalDate lastDay = LocalDate.parse("2028-09-23");
        Instant c4p = Instant.parse("2028-02-01T00:00:00Z");
        create(new EditionRequest(2028, "Confitura", start, lastDay, c4p, c4p)).andExpect(status().isBadRequest());
        create(new EditionRequest(2028, "Confitura", start, lastDay, c4p, c4p.minusSeconds(1)))
                .andExpect(status().isBadRequest());
        // The conference is in Warsaw, where 23:30 UTC on the 21st is already the 22nd.
        create(new EditionRequest(2028, "Confitura", Instant.parse("2028-09-21T23:30:00Z"), LocalDate.parse("2028-09-21"),
                null, null)).andExpect(status().isBadRequest());

        create(new EditionRequest(2028, "Confitura", start, LocalDate.parse("2028-09-22"), c4p, c4p.plusSeconds(1)))
                .andExpect(status().isCreated());
    }

    @Test
    void refusesAYearAnotherEditionHas() throws Exception {
        create(inYear(2027)).andExpect(status().isConflict());
        create(inYear(2026)).andExpect(status().isConflict());
    }

    @Test
    void updatesAnEditionsNameDatesAndC4pPeriod() throws Exception {
        update(past, new EditionRequest(2025, "Confitura XV", Instant.parse("2025-06-27T07:00:00Z"),
                LocalDate.parse("2025-06-28"), Instant.parse("2025-01-10T00:00:00Z"), Instant.parse("2025-03-10T00:00:00Z")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(past.getId())))
                .andExpect(jsonPath("$.year", is(2025)))
                .andExpect(jsonPath("$.current", is(false)));

        mockMvc.perform(get("/editions/" + past.getId()).with(admin()))
                .andExpect(jsonPath("$.name", is("Confitura XV")))
                .andExpect(jsonPath("$.conferenceStart", is("2025-06-27T07:00:00Z")))
                .andExpect(jsonPath("$.lastDay", is("2025-06-28")))
                .andExpect(jsonPath("$.c4pStart", is("2025-01-10T00:00:00Z")))
                .andExpect(jsonPath("$.c4pEnd", is("2025-03-10T00:00:00Z")))
                .andExpect(jsonPath("$.current", is(false)));
    }

    @Test
    void anUpdateWithoutAC4pPeriodClosesIt() throws Exception {
        update(next, inYear(2027)).andExpect(status().isOk());

        mockMvc.perform(get("/editions/" + next.getId()).with(admin()))
                .andExpect(jsonPath("$.c4pStart", nullValue()))
                .andExpect(jsonPath("$.c4pEnd", nullValue()));
    }

    @Test
    void updatingTheCurrentEditionsDatesKeepsItCurrent() throws Exception {
        update(current, new EditionRequest(2026, current.getName(), Instant.parse("2026-06-25T07:00:00Z"),
                LocalDate.parse("2026-06-26"), Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-03-01T00:00:00Z")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current", is(true)));

        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(jsonPath("$[*].current", contains(false, true, false)))
                .andExpect(jsonPath("$[1].conferenceStart", is("2026-06-25T07:00:00Z")))
                .andExpect(jsonPath("$[1].c4pEnd", is("2026-03-01T00:00:00Z")));
    }

    @Test
    void refusesAnUpdateChangingTheYearOrOfAnUnknownEdition() throws Exception {
        update(past, inYear(2024)).andExpect(status().isBadRequest());
        update(past, new EditionRequest(null, "Confitura 2025", Instant.parse("2025-09-22T07:00:00Z"),
                LocalDate.parse("2025-09-23"), null, null)).andExpect(status().isBadRequest());
        mockMvc.perform(put("/editions/does-not-exist").with(admin()).contentType(APPLICATION_JSON).content(json(inYear(2025))))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(jsonPath("$[*].year", contains(2027, 2026, 2025)));
    }

    @Test
    void refusesAnUpdateWithInvalidDatesOrWithoutAName() throws Exception {
        Instant start = Instant.parse("2025-09-22T07:00:00Z");
        LocalDate lastDay = LocalDate.parse("2025-09-23");
        Instant c4p = Instant.parse("2025-02-01T00:00:00Z");
        update(past, new EditionRequest(2025, "Renamed", start, lastDay, c4p, c4p)).andExpect(status().isBadRequest());
        update(past, new EditionRequest(2025, "Renamed", start, lastDay, c4p, c4p.minusSeconds(1)))
                .andExpect(status().isBadRequest());
        // The conference is in Warsaw, where 23:30 UTC on the 21st is already the 22nd.
        update(past, new EditionRequest(2025, "Renamed", Instant.parse("2025-09-21T23:30:00Z"), LocalDate.parse("2025-09-21"),
                null, null)).andExpect(status().isBadRequest());
        update(past, new EditionRequest(2025, null, start, lastDay, null, null)).andExpect(status().isBadRequest());

        mockMvc.perform(get("/editions/" + past.getId()).with(admin()))
                .andExpect(jsonPath("$.name", is("Confitura 2025")));
    }

    @Test
    void deletesAnEdition() throws Exception {
        mockMvc.perform(delete("/editions/" + past.getId()).with(admin()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(jsonPath("$[*].year", contains(2027, 2026)));
        mockMvc.perform(delete("/editions/does-not-exist").with(admin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void refusesToDeleteTheCurrentEditionOrOneWithATalkWorkshopOrAgendaDay() throws Exception {
        Edition withDay = persist(edition(2028));
        persist(presentation(past).setWorkshop(false));
        persist(presentation(next).setWorkshop(true));
        persist(new Day().setId("edition-test-day").setDate(LocalDate.parse("2028-09-22")).setLabel("Day 1")
                .setDisplayOrder(1).setEdition(withDay));

        for (Edition kept : List.of(editionRepository.findCurrent(), past, next, withDay)) {
            mockMvc.perform(delete("/editions/" + kept.getId()).with(admin()))
                    .andExpect(status().isConflict());
        }

        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(jsonPath("$[*].year", contains(2028, 2027, 2026, 2025)));
    }

    @Test
    void refusesEveryoneButAdmins() throws Exception {
        RequestPostProcessor anonymous = request -> request;
        String pastUrl = "/editions/" + past.getId();
        for (RequestPostProcessor nonAdmin : List.of(anonymous, user("not-an-admin"))) {
            mockMvc.perform(get("/editions").with(nonAdmin)).andExpect(status().isForbidden());
            mockMvc.perform(get(pastUrl).with(nonAdmin)).andExpect(status().isForbidden());
            mockMvc.perform(post("/editions").with(nonAdmin).contentType(APPLICATION_JSON).content(json(inYear(2028))))
                    .andExpect(status().isForbidden());
            mockMvc.perform(put(pastUrl).with(nonAdmin).contentType(APPLICATION_JSON).content(json(inYear(2025))))
                    .andExpect(status().isForbidden());
            mockMvc.perform(delete(pastUrl).with(nonAdmin)).andExpect(status().isForbidden());
            mockMvc.perform(post(pastUrl + "/make-current").with(nonAdmin)).andExpect(status().isForbidden());
        }

        mockMvc.perform(get("/editions").with(admin()))
                .andExpect(jsonPath("$[*].year", contains(2027, 2026, 2025)))
                .andExpect(jsonPath("$[*].current", contains(false, true, false)));
    }

    private static Edition edition(int year) {
        return new Edition().setYear(year).setName("Confitura " + year)
                .setConferenceStart(Instant.parse(year + "-09-22T07:00:00Z")).setLastDay(LocalDate.parse(year + "-09-23"));
    }

    private static Presentation presentation(Edition edition) {
        return new Presentation().setTitle("A talk").setShortDescription("Short").setDescription("Long")
                .setLevel("easy").setLanguage("pl").setEdition(edition);
    }

    // A vote for a new talk in the Edition, cast from the IP address.
    private Vote vote(Edition edition, String ip) {
        Presentation talk = persist(presentation(edition));
        created.push(talk);
        Vote vote = persist(new Vote().setToken("voter").setOrder(0).setPresentation(talk).setClient(ip));
        created.push(vote);
        return vote;
    }

    private static EditionRequest inYear(int year) {
        return new EditionRequest(year, "Confitura " + year, Instant.parse(year + "-09-22T07:00:00Z"),
                LocalDate.parse(year + "-09-23"), null, null);
    }

    private ResultActions create(EditionRequest request) throws Exception {
        return mockMvc.perform(post("/editions").with(admin()).contentType(APPLICATION_JSON).content(json(request)));
    }

    private ResultActions update(Edition edition, EditionRequest request) throws Exception {
        return mockMvc.perform(put("/editions/" + edition.getId()).with(admin()).contentType(APPLICATION_JSON)
                .content(json(request)));
    }

    private ResultActions makeCurrent(Edition edition) throws Exception {
        return mockMvc.perform(post("/editions/" + edition.getId() + "/make-current").with(admin()));
    }

    private <T> T persist(T entity) {
        txTemplate.executeWithoutResult(status -> entityManager.persist(entity));
        return entity;
    }
}
