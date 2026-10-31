package pl.confitura.jelatyna.conference.voting.web;

import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.conference.voting.domain.Vote;
import pl.confitura.jelatyna.conference.voting.domain.VoteRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Autowired
    TransactionTemplate txTemplate;
    @Autowired
    EntityManager entityManager;

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

        // The response is read back through an Edition-scoped query, so check what was stored too.
        assertThat(voteRepository.findAll())
                .filteredOn(vote -> "new-voter".equals(vote.getToken()))
                .extracting(vote -> vote.getPresentation().getId())
                .contains(currentTalk.getId())
                .doesNotContain(pastTalk.getId());
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
    void startingAgainReturnsTheSameBallot() throws Exception {
        talk(editionRepository.findCurrent());
        String first = start("voter-starting-again");

        assertThat(voteIds(start("voter-starting-again"))).isEqualTo(voteIds(first));
    }

    // Pressing Enter on vote-for-papers' Start button sent two starts at once, and each created a ballot.
    // The starts must run in transactions of their own, so this test commits and then removes its votes.
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void startingSeveralTimesAtOnceGivesTheVoterOneBallot() throws Exception {
        List<String> talks = StreamSupport.stream(presentationRepository.findAllForV4p().spliterator(), false)
                .map(Presentation::getId).toList();
        CyclicBarrier together = new CyclicBarrier(4);
        Callable<String> start = () -> {
            together.await();
            return start("voter-starting-at-once");
        };
        List<String> ballots = new ArrayList<>();
        try (ExecutorService starts = Executors.newFixedThreadPool(4)) {
            for (Future<String> ballot : starts.invokeAll(Collections.nCopies(4, start))) {
                ballots.add(ballot.get());
            }
        } finally {
            txTemplate.executeWithoutResult(status -> entityManager
                    .createQuery("delete from Vote v where v.token = 'voter-starting-at-once'").executeUpdate());
        }

        assertThat(talks).isNotEmpty();
        assertThat(ballots).allSatisfy(ballot -> {
            assertThat(JsonPath.<List<String>>read(ballot, "$[*].presentation.id")).containsExactlyInAnyOrderElementsOf(talks);
            assertThat(voteIds(ballot)).isEqualTo(voteIds(ballots.getFirst()));
        });
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

    private String start(String token) throws Exception {
        return mockMvc.perform(post("/votes/start/" + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private static List<String> voteIds(String ballot) {
        return JsonPath.read(ballot, "$[*].id");
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
