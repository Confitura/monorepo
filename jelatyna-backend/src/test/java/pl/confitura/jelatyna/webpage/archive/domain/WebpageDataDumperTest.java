package pl.confitura.jelatyna.webpage.archive.domain;

import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.FileSystemUtils;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.Day;
import pl.confitura.jelatyna.agenda.DayRepository;
import pl.confitura.jelatyna.edition.Edition;
import pl.confitura.jelatyna.edition.EditionRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.presentation.rating.Rate;
import pl.confitura.jelatyna.presentation.rating.RateRepository;
import pl.confitura.jelatyna.presentation.rating.RateValue;
import pl.confitura.jelatyna.platform.storage.ResourceConfigurationProperties;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;
import pl.confitura.jelatyna.webpage.page.domain.Page;
import pl.confitura.jelatyna.webpage.page.domain.PageRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WebpageDataDumperTest extends BaseIntegrationTest {

    @Autowired
    private WebpageDataDumper webpageDataDumper;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private RateRepository rateRepository;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private ResourceConfigurationProperties resources;

    private Edition edition2026;
    private Edition edition2031;
    private Path folder;
    // Newest first, so a talk goes before its speaker.
    private final Deque<Object> created = new ArrayDeque<>();

    // Edition 2026 stays in the database, so it is the other Edition whose data must stay out.
    @BeforeEach
    void makeEdition2031Current() throws Exception {
        folder = Path.of(resources.folder(), "edition-2031");
        FileSystemUtils.deleteRecursively(folder);
        SecurityHelper.asAdmin();
        edition2026 = editionRepository.save(editionRepository.findCurrent().setCurrent(false));
        edition2031 = editionRepository.save(new Edition()
                .setYear(2031)
                .setName("Confitura 2031")
                .setConferenceStart(Instant.parse("2031-06-27T07:00:00Z"))
                .setLastDay(LocalDate.of(2031, 6, 28))
                .setCurrent(true));
        SecurityHelper.cleanSecurity();
    }

    @AfterEach
    void makeEdition2026CurrentAgain() {
        SecurityHelper.asAdmin();
        editionRepository.save(edition2031.setCurrent(false));
        editionRepository.save(edition2026.setCurrent(true));
        SecurityHelper.cleanSecurity();
        txTemplate.executeWithoutResult(status -> {
            created.forEach(entity -> entityManager.remove(entityManager.merge(entity)));
            entityManager.remove(entityManager.find(Edition.class, edition2031.getId()));
        });
    }

    @Test
    void shouldBumpAllWithoutErrors() {
        webpageDataDumper.dumpAll();
    }

    @Test
    void editionJsonCarriesTheCurrentEditionsYearNameAndConferenceDates() throws Exception {
        webpageDataDumper.dumpAll();

        JSONAssert.assertEquals("""
                {"year": 2031, "name": "Confitura 2031", "conferenceStart": "2031-06-27T09:00:00+02:00", "lastDay": "2031-06-28"}
                """, Files.readString(folder.resolve("edition.json")), JSONCompareMode.STRICT);
    }

    @Test
    void theDumpsThatAreNotPerEditionGoToTheCurrentEditionsFolderToo() {
        webpageDataDumper.dumpAll();

        assertThat(List.of("users/search/admins.json", "users/search/volunteers.json", "faq/entries.json",
                "partners/list.json", "news.json"))
                .allSatisfy(file -> assertThat(folder.resolve(file)).exists());
    }

    @Test
    void eachPageIsDumpedAsItsContent() {
        Page page = new Page();
        page.setId("dumped-page");
        page.setContent("# About");
        SecurityHelper.asAdmin();
        created.push(pageRepository.save(page));
        SecurityHelper.cleanSecurity();

        webpageDataDumper.dumpAll();

        assertThat(folder.resolve("pages/dumped-page.json")).hasContent("\"# About\"");
    }

    @Test
    void talksAndWorkshopsOfOtherEditionsAreLeftOut() throws Exception {
        User speaker = speaker("Returning Speaker");
        talk("This Year's Talk", false, edition2031, speaker);
        talk("This Year's Workshop", true, edition2031, speaker);
        talk("Last Year's Talk", false, edition2026, speaker);
        talk("Last Year's Workshop", true, edition2026, speaker);

        webpageDataDumper.dumpAll();

        assertThat(read("presentations/accepted.json", "$[*].title")).containsExactly("This Year's Talk");
        assertThat(read("workshops/accepted.json", "$[*].title")).containsExactly("This Year's Workshop");
    }

    @Test
    void speakersShowOnlyTheirTalksInTheCurrentEdition() throws Exception {
        User returning = speaker("Returning Speaker");
        User coSpeaker = speaker("Co-speaker");
        User pastOnly = speaker("Past Speaker");
        talk("This Year's Talk", false, edition2031, returning, coSpeaker);
        talk("This Year's Workshop", true, edition2031, returning);
        talk("Last Year's Talk", false, edition2026, returning);
        talk("Last Year's Other Talk", false, edition2026, pastOnly);

        webpageDataDumper.dumpAll();

        assertThat(read("users/search/speakers.json", "$[*].name")).containsExactlyInAnyOrder("Returning Speaker", "Co-speaker");
        assertThat(read("users/" + returning.getId() + "/public.json", "$.presentations[*].name"))
                .containsExactlyInAnyOrder("This Year's Talk", "This Year's Workshop");
        assertThat(read("users/" + coSpeaker.getId() + "/public.json", "$.presentations[*].name"))
                .containsExactly("This Year's Talk");
        assertThat(folder.resolve("users/" + pastOnly.getId() + "/public.json")).doesNotExist();
    }

    @Test
    void theAgendaHasOnlyTheCurrentEditionsDaysAndTalks() throws Exception {
        User speaker = speaker("Returning Speaker");
        talk("This Year's Talk", false, edition2031, speaker);
        talk("Last Year's Talk", false, edition2026, speaker);
        day("day-2031", edition2031);
        day("day-of-2026", edition2026);

        webpageDataDumper.dumpAll();

        assertThat(folder.resolve("agenda").toFile().list()).containsExactly("day-2031.json");
        assertThat(read("agenda/day-2031.json", "$.presentations[*].title")).containsExactly("This Year's Talk");
    }

    @Test
    void noDumpCarriesAReviewerToken() throws Exception {
        Presentation talk = talk("Rated Talk", false, edition2031, speaker("Rated Speaker"));
        day("day-2031", edition2031);
        String reviewerToken = UUID.randomUUID().toString();
        created.push(rateRepository.save(new Rate().setValue(RateValue.AWESOME).setComment("Great talk")
                .setReviewerToken(reviewerToken).setPresentation(talk)));

        webpageDataDumper.dumpAll();

        List<Path> dumps;
        try (var files = Files.walk(folder)) {
            dumps = files.filter(Files::isRegularFile).toList();
        }
        assertThat(dumps).isNotEmpty().allSatisfy(dump ->
                assertThat(Files.readString(dump)).doesNotContain(reviewerToken, "reviewerToken"));
    }

    private User speaker(String name) {
        User speaker = userRepository.save(new User().setName(name));
        created.push(speaker);
        return speaker;
    }

    private Presentation talk(String title, boolean workshop, Edition edition, User... speakers) {
        Presentation talk = new Presentation()
                .setTitle(title)
                .setShortDescription("short")
                .setDescription("long")
                .setLevel("beginner")
                .setLanguage("Polish")
                .setWorkshop(workshop)
                .setEdition(edition);
        Arrays.stream(speakers).forEach(talk::setSpeaker);
        talk.setAccepted(true);
        Presentation saved = presentationRepository.save(talk);
        created.push(saved);
        return saved;
    }

    private void day(String id, Edition edition) {
        SecurityHelper.asAdmin();
        created.push(dayRepository.save(new Day().setId(id).setLabel(id).setDate(LocalDate.of(edition.getYear(), 6, 27))
                .setDisplayOrder(1).setEdition(edition)));
        SecurityHelper.cleanSecurity();
    }

    private List<String> read(String file, String jsonPath) throws Exception {
        return JsonPath.read(Files.readString(folder.resolve(file)), jsonPath);
    }
}
