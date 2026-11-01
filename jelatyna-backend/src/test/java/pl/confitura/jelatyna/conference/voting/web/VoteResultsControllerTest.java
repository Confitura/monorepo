package pl.confitura.jelatyna.conference.voting.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.conference.voting.domain.Vote;
import pl.confitura.jelatyna.conference.voting.domain.VoteRepository;
import pl.confitura.jelatyna.identity.user.web.SecurityHelper;
import pl.confitura.jelatyna.conference.presentation.domain.PreSelectionStatus;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.identity.user.web.SecurityHelper.admin;

@Transactional
class VoteResultsControllerTest extends BaseIntegrationTest {

    @Autowired
    PresentationRepository presentationRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EditionRepository editionRepository;

    private Presentation presentation;

    @BeforeEach
    void setup() {
        presentation = createPresentation("Reactive Streams", "Jane Doe");
    }

    @Test
    void adminShouldGetAggregatedResultsWithPerVoterScores() throws Exception {
        //given four voters rated the presentation
        voteRepository.saveAll(List.of(
                vote("A", 1),
                vote("B", 1),
                vote("C", -1),
                vote("D", 0)
        ));

        //when admin asks for results scoped with per-voter columns for tokens A and C
        String row = "$[?(@.presentationId=='" + presentation.getId() + "')]";

        mockMvc.perform(get("/votes/results").param("tokens", "A", "C").with(admin()))
                .andExpect(status().isOk())
                //aggregates cover ALL voters
                .andExpect(jsonPath(row + ".total", contains(4)))
                .andExpect(jsonPath(row + ".score", contains(1)))
                .andExpect(jsonPath(row + ".positive", contains(2)))
                .andExpect(jsonPath(row + ".negative", contains(1)))
                .andExpect(jsonPath(row + ".neutral", contains(1)))
                .andExpect(jsonPath(row + ".flatSpeakers", contains("Jane Doe")))
                //per-voter columns only for the requested tokens
                .andExpect(jsonPath(row + ".voterScores.A", contains(1)))
                .andExpect(jsonPath(row + ".voterScores.C", contains(-1)))
                .andExpect(jsonPath(row + ".voterScores.B", empty()));
    }

    @Test
    void userShouldNotBeAllowedToGetResults() throws Exception {
        mockMvc.perform(get("/votes/results"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldSetPreSelectionStatusAndComment() throws Exception {
        mockMvc.perform(post("/presentations/" + presentation.getId() + "/pre-selection")
                        .content("{\"status\":\"IN_RESERVE\",\"comment\":\"solid backup talk\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(admin()))
                .andExpect(status().isOk());

        Presentation reloaded = presentationRepository.findById(presentation.getId());
        assertThat(reloaded.getPreSelectionStatus()).isEqualTo(PreSelectionStatus.IN_RESERVE);
        assertThat(reloaded.getPreSelectionComment()).isEqualTo("solid backup talk");
    }

    @Test
    void resultShouldFlagWhenSpeakerAlreadyHasPreSelectedPresentation() throws Exception {
        //given a speaker with a pre-approved presentation and another (voted) presentation
        User speaker = userRepository.save(new User().setName("Busy Speaker"));
        Presentation preSelected = createPresentation("Pre-approved Talk", speaker);
        preSelected.setPreSelectionStatus(PreSelectionStatus.PRE_APPROVED);
        presentationRepository.save(preSelected);
        Presentation other = createPresentation("Other Talk", speaker);
        voteRepository.saveAll(List.of(new Vote().setToken("Z").setRate(1).setPresentation(other)));

        mockMvc.perform(get("/votes/results").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.presentationId=='" + other.getId() + "')].speakerHasPreSelectedPresentation",
                        contains(true)));
    }

    @Test
    void resultsCoverOnlyTheCurrentEditionsTalks() throws Exception {
        //given a talk of a past Edition was voted on too
        Presentation pastTalk = createPresentation("Last Year's Talk", "John Doe").setEdition(pastEdition());
        voteRepository.saveAll(List.of(vote("A", 1), new Vote().setToken("A").setRate(1).setPresentation(pastTalk)));

        mockMvc.perform(get("/votes/results").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].presentationId", hasItem(presentation.getId())))
                .andExpect(jsonPath("$[*].presentationId", not(hasItem(pastTalk.getId()))));
    }

    @Test
    void resultShouldNotFlagASpeakerWhosePreApprovedTalkIsInAPastEdition() throws Exception {
        User speaker = userRepository.save(new User().setName("Returning Speaker"));
        createPresentation("Last Year's Talk", speaker).setEdition(pastEdition())
                .setPreSelectionStatus(PreSelectionStatus.PRE_APPROVED);
        Presentation thisYears = createPresentation("This Year's Talk", speaker);
        voteRepository.saveAll(List.of(new Vote().setToken("Z").setRate(1).setPresentation(thisYears)));

        mockMvc.perform(get("/votes/results").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.presentationId=='" + thisYears.getId() + "')].speakerHasPreSelectedPresentation",
                        contains(false)));
    }

    private Edition pastEdition() {
        SecurityHelper.asAdmin();
        Edition past = editionRepository.save(new Edition().setYear(2025).setName("Confitura 2025")
                .setConferenceStart(Instant.parse("2025-07-05T07:00:00Z")).setLastDay(LocalDate.parse("2025-07-05")));
        SecurityHelper.cleanSecurity();
        return past;
    }

    private Vote vote(String token, int rate) {
        return new Vote().setToken(token).setRate(rate).setPresentation(presentation);
    }

    private Presentation createPresentation(String title, String speakerName) {
        return createPresentation(title, userRepository.save(new User().setName(speakerName)));
    }

    private Presentation createPresentation(String title, User speaker) {
        Presentation p = new Presentation();
        p.setTitle(title);
        p.setShortDescription("short");
        p.setDescription("description");
        p.setLevel("advanced");
        p.setLanguage("en");
        p.setSpeaker(speaker);
        p.setEdition(editionRepository.findCurrent());
        return presentationRepository.save(p);
    }
}
