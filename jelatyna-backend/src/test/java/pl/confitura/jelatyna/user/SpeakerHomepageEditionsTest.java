package pl.confitura.jelatyna.user;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.user;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.edition.Edition;
import pl.confitura.jelatyna.edition.EditionRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

// The speaker homepage shows one Edition's talks and workshops, the Current Edition's by default (ADR-0004).
@Transactional
class SpeakerHomepageEditionsTest extends BaseIntegrationTest {

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private UserRepository userRepository;

    private User speaker;
    private Edition current;
    private Edition past;

    @BeforeEach
    void editions() {
        speaker = userRepository.save(new User().setName("Speaker").setEmail("speaker@homepage-editions.test"));
        current = editionRepository.findCurrent();
        SecurityHelper.asAdmin();
        past = editionRepository.save(edition(2025));
        editionRepository.save(edition(2024));
        SecurityHelper.cleanSecurity();
    }

    @Test
    void listsTheCurrentEditionsTalksAndWorkshopsByDefault() throws Exception {
        talk("This year's talk", current, false);
        talk("Last year's talk", past, false);
        talk("This year's workshop", current, true);
        talk("Last year's workshop", past, true);

        mockMvc.perform(get("/users/" + speaker.getId() + "/presentations").with(user(speaker)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", contains("This year's talk")));
        mockMvc.perform(get("/users/" + speaker.getId() + "/workshops").with(user(speaker)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", contains("This year's workshop")));
    }

    @Test
    void listsTheTalksAndWorkshopsOfTheEditionAsked() throws Exception {
        talk("This year's talk", current, false);
        talk("Last year's talk", past, false);
        talk("This year's workshop", current, true);
        talk("Last year's workshop", past, true);

        mockMvc.perform(get("/users/" + speaker.getId() + "/presentations").param("editionId", past.getId()).with(user(speaker)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", contains("Last year's talk")));
        mockMvc.perform(get("/users/" + speaker.getId() + "/workshops").param("editionId", past.getId()).with(user(speaker)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", contains("Last year's workshop")));
    }

    // The homepage's Edition picker lists these, and appears only when there is more than one.
    @Test
    void listsTheCurrentEditionAndEveryEditionTheSpeakerHasTalksOrWorkshopsInNewestFirst() throws Exception {
        talk("Last year's workshop", past, true);

        mockMvc.perform(get("/users/" + speaker.getId() + "/editions").with(user(speaker)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].year", contains(2026, 2025)))
                .andExpect(jsonPath("$[*].current", contains(true, false)));
    }

    @Test
    void listsOnlyTheCurrentEditionForASpeakerWithoutPastTalks() throws Exception {
        talk("This year's talk", current, false);

        mockMvc.perform(get("/users/" + speaker.getId() + "/editions").with(user(speaker)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].year", contains(2026)));
    }

    @Test
    void anAdminGetsAUsersEditions() throws Exception {
        talk("Last year's talk", past, false);

        mockMvc.perform(get("/users/" + speaker.getId() + "/editions").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].year", contains(2026, 2025)));
    }

    @Test
    void anotherUserCannotGetASpeakersEditions() throws Exception {
        User other = userRepository.save(new User().setName("Other").setEmail("other@homepage-editions.test"));

        mockMvc.perform(get("/users/" + speaker.getId() + "/editions").with(user(other)))
                .andExpect(status().isForbidden());
    }

    private void talk(String title, Edition edition, boolean workshop) {
        presentationRepository.save(new Presentation()
                .setTitle(title)
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("beginner")
                .setLanguage("Polish")
                .setWorkshop(workshop)
                .setSpeakers(new HashSet<>(Set.of(speaker)))
                .setEdition(edition));
    }

    private static Edition edition(int year) {
        return new Edition()
                .setYear(year)
                .setName("Confitura " + year)
                .setConferenceStart(Instant.parse(year + "-06-28T07:00:00Z"))
                .setLastDay(LocalDate.of(year, 6, 28));
    }
}
