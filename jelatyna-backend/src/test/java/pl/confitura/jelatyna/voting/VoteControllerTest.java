package pl.confitura.jelatyna.voting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.edition.Edition;
import pl.confitura.jelatyna.edition.EditionRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class VoteControllerTest extends BaseIntegrationTest {

    @Autowired
    PresentationRepository presentationRepository;
    @Autowired
    EditionRepository editionRepository;
    @Autowired
    VoteRepository voteRepository;

    @Test
    public void shouldStartVoting() throws Exception {

        //when
        ResultActions $ = mockMvc.perform(post("/votes/start/token"));

        //then
        $.andExpect(status().isOk());

    }

    @Test
    void startingVotingCreatesVotesForTheCurrentEditionsTalksOnly() throws Exception {
        Presentation currentTalk = talk(editionRepository.findCurrent());
        Presentation pastTalk = talk(pastEdition());

        mockMvc.perform(post("/votes/start/new-voter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].presentation.id", hasItem(currentTalk.getId())))
                .andExpect(jsonPath("$[*].presentation.id", not(hasItem(pastTalk.getId()))));
    }

    @Test
    void startingVotingWithATokenThatVotedInAPastEditionCreatesVotesForTheCurrentEdition() throws Exception {
        Presentation currentTalk = talk(editionRepository.findCurrent());
        Presentation pastTalk = talk(pastEdition());
        voteRepository.saveAll(List.of(new Vote().setToken("returning-voter").setOrder(0).setPresentation(pastTalk)));

        mockMvc.perform(post("/votes/start/returning-voter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].presentation.id", hasItem(currentTalk.getId())))
                .andExpect(jsonPath("$[*].presentation.id", not(hasItem(pastTalk.getId()))));
    }

    @Test
    void savingAVoteForACurrentEditionTalkIsAccepted() throws Exception {
        Vote vote = unratedVote(talk(editionRepository.findCurrent()));

        saveVote(vote).andExpect(status().isOk());
    }

    @Test
    void savingAVoteForATalkOutsideTheCurrentEditionIsRefused() throws Exception {
        Vote vote = unratedVote(talk(pastEdition()));

        saveVote(vote).andExpect(status().isForbidden());
    }

    private Vote unratedVote(Presentation talk) {
        return voteRepository.saveAll(List.of(new Vote().setToken("voter").setOrder(0).setPresentation(talk)))
                .iterator().next();
    }

    private ResultActions saveVote(Vote vote) throws Exception {
        return mockMvc.perform(post("/votes")
                .content(json(new VoteController.VoteRequest(vote.getId(), 1)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private Edition pastEdition() {
        SecurityHelper.asAdmin();
        Edition edition = editionRepository.save(new Edition()
                .setYear(2025)
                .setName("Confitura 2025")
                .setConferenceStart(Instant.parse("2025-06-28T07:00:00Z"))
                .setLastDay(LocalDate.of(2025, 6, 28)));
        SecurityHelper.cleanSecurity();
        return edition;
    }

    private Presentation talk(Edition edition) {
        return presentationRepository.save(new Presentation()
                .setTitle("Talk")
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("easy")
                .setLanguage("pl")
                .setEdition(edition));
    }
}
