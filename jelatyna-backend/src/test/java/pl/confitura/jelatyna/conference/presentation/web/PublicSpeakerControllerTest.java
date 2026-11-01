package pl.confitura.jelatyna.conference.presentation.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Map;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import com.jayway.jsonpath.JsonPath;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.identity.user.web.SecurityHelper;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

// The speakers' public profiles: the list shows each speaker of an accepted talk with those talks, of any Edition;
// a speaker's own profile lists every talk they give.
class PublicSpeakerControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate txTemplate;

    // Committed, so each request reads them afresh; newest first, so a talk goes before its speakers and Edition.
    private final Deque<Object> created = new ArrayDeque<>();

    @AfterEach
    void removeWhatTheTestCreated() {
        txTemplate.executeWithoutResult(status -> created.forEach(entity -> entityManager.remove(entityManager.merge(entity))));
    }

    @Test
    void theSpeakerListShowsEachSpeakerOfAnAcceptedTalkWithOnlyTheirAcceptedTalks() throws Exception {
        User speaker = speaker("Speaker");
        User coSpeaker = speaker("Co-speaker");
        User notAccepted = speaker("Not accepted");
        Presentation talk = talk("Talk", true, false, editionRepository.findCurrent(), speaker, coSpeaker);
        Presentation workshop = talk("Past workshop", true, true, pastEdition(), speaker);
        talk("Reported talk", false, false, editionRepository.findCurrent(), speaker, notAccepted);

        String body = mockMvc.perform(get("/users/search/speakers"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals("[" + profile(speaker, talk, workshop) + "]", only(body, speaker), JSONCompareMode.NON_EXTENSIBLE);
        JSONAssert.assertEquals("[" + profile(coSpeaker, talk) + "]", only(body, coSpeaker), JSONCompareMode.NON_EXTENSIBLE);
        JSONAssert.assertEquals("[]", only(body, notAccepted), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void aSpeakersPublicProfileListsEveryTalkTheyGive() throws Exception {
        User speaker = speaker("Speaker");
        Presentation talk = talk("Talk", true, false, editionRepository.findCurrent(), speaker);
        Presentation reported = talk("Past reported workshop", false, true, pastEdition(), speaker);

        String body = mockMvc.perform(get("/users/" + speaker.getId() + "/public"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(profile(speaker, talk, reported), body, JSONCompareMode.NON_EXTENSIBLE);
    }

    private String only(String speakers, User speaker) {
        return json(JsonPath.read(speakers, "$[?(@.id == '" + speaker.getId() + "')]"));
    }

    private String profile(User speaker, Presentation... talks) {
        return json(Map.of("id", speaker.getId(), "name", speaker.getName(), "bio", "Bio", "twitter", "tw", "github", "gh",
                "www", "https://www.test", "photo", "photo.png", "presentations", Arrays.stream(talks)
                        .map(talk -> Map.of("id", talk.getId(), "name", talk.getTitle(), "isWorkshop", talk.isWorkshop())).toList()));
    }

    private User speaker(String name) {
        return keep(userRepository.save(new User().setName(name).setBio("Bio").setTwitter("tw").setGithub("gh")
                .setWww("https://www.test").setPhoto("photo.png")));
    }

    private Presentation talk(String title, boolean accepted, boolean workshop, Edition edition, User... speakers) {
        Presentation talk = new Presentation()
                .setTitle(title)
                .setShortDescription("short")
                .setDescription("long")
                .setLevel("beginner")
                .setLanguage("Polish")
                .setWorkshop(workshop)
                .setEdition(edition);
        Arrays.stream(speakers).forEach(talk::setSpeaker);
        talk.setAccepted(accepted);
        return keep(presentationRepository.save(talk));
    }

    private Edition pastEdition() {
        SecurityHelper.asAdmin();
        Edition edition = editionRepository.save(new Edition()
                .setYear(2025)
                .setName("Confitura 2025")
                .setConferenceStart(Instant.parse("2025-06-28T07:00:00Z"))
                .setLastDay(LocalDate.of(2025, 6, 28)));
        SecurityHelper.cleanSecurity();
        return keep(edition);
    }

    private <T> T keep(T entity) {
        created.push(entity);
        return entity;
    }
}
