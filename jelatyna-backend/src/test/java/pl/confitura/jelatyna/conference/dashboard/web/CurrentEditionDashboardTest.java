package pl.confitura.jelatyna.conference.dashboard.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.dashboard.domain.SubmittedStats;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.conference.voting.domain.Vote;
import pl.confitura.jelatyna.conference.voting.domain.VoteRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;

// The dashboard counts only the Current Edition's submissions and votes (ADR-0004).
@Transactional
class CurrentEditionDashboardTest extends BaseIntegrationTest {

    @Autowired
    EditionRepository editionRepository;
    @Autowired
    PresentationRepository presentationRepository;
    @Autowired
    VoteRepository voteRepository;

    private Edition current;
    private Edition past;

    @BeforeEach
    void createAPastEdition() {
        SecurityHelper.asAdmin();
        past = editionRepository.save(new Edition().setYear(2025).setName("Confitura 2025")
                .setConferenceStart(Instant.parse("2025-07-05T07:00:00Z")).setLastDay(LocalDate.parse("2025-07-05")));
        SecurityHelper.cleanSecurity();
        current = editionRepository.findCurrent();
    }

    @Test
    void submissionStatsCountOnlyTheCurrentEditionsTalksAndWorkshops() throws Exception {
        SubmittedStats before = submissionStats();
        presentation(current, false);
        presentation(current, true);
        presentation(past, false);
        presentation(past, true);

        SubmittedStats after = submissionStats();

        assertThat(after.total()).isEqualTo(before.total() + 2);
        assertThat(after.presentations()).isEqualTo(before.presentations() + 1);
        assertThat(after.workshops()).isEqualTo(before.workshops() + 1);
    }

    @Test
    void votesOverTimeCountOnlyVotesOnTheCurrentEditionsTalks() throws Exception {
        voteRepository.saveAll(List.of(
                vote(presentation(current, false), LocalDateTime.parse("2026-03-01T10:00:07")),
                vote(presentation(past, false), LocalDateTime.parse("2025-03-01T10:00:07"))));

        mockMvc.perform(get("/dashboard/votes").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*][0]", hasItem("2026-03-01T10:00:07")))
                .andExpect(jsonPath("$[*][0]", not(hasItem("2025-03-01T10:00:07"))));
    }

    private SubmittedStats submissionStats() throws Exception {
        String body = mockMvc.perform(get("/dashboard/submissions").with(admin()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return fromJson(body, SubmittedStats.class);
    }

    private Presentation presentation(Edition edition, boolean workshop) {
        return presentationRepository.save(new Presentation()
                .setTitle(workshop ? "Workshop" : "Talk")
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("easy")
                .setLanguage("en")
                .setWorkshop(workshop)
                .setEdition(edition));
    }

    private Vote vote(Presentation presentation, LocalDateTime date) {
        return new Vote().setToken("A").setRate(1).setPresentation(presentation).setVoteDate(date);
    }
}
