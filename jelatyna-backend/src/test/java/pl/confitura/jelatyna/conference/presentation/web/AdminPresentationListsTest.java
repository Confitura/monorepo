package pl.confitura.jelatyna.conference.presentation.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.conference.rating.domain.Rate;
import pl.confitura.jelatyna.conference.rating.domain.RateRepository;
import pl.confitura.jelatyna.conference.rating.domain.RateValue;

import java.time.Instant;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;

// Admin pages show only the Current Edition (ADR-0004).
@Transactional
class AdminPresentationListsTest extends BaseIntegrationTest {

    @Autowired
    PresentationRepository presentationRepository;
    @Autowired
    RateRepository rateRepository;
    @Autowired
    EditionRepository editionRepository;

    private Presentation currentTalk;
    private Presentation currentWorkshop;
    private Presentation pastTalk;
    private Presentation pastWorkshop;

    @BeforeEach
    void createTalksAndWorkshopsInTheCurrentAndAPastEdition() {
        SecurityHelper.asAdmin();
        Edition past = editionRepository.save(new Edition().setYear(2025).setName("Confitura 2025")
                .setConferenceStart(Instant.parse("2025-07-05T07:00:00Z")).setLastDay(LocalDate.parse("2025-07-05")));
        SecurityHelper.cleanSecurity();
        Edition current = editionRepository.findCurrent();
        currentTalk = presentation("This Year's Talk", current, false);
        currentWorkshop = presentation("This Year's Workshop", current, true);
        pastTalk = presentation("Last Year's Talk", past, false);
        pastWorkshop = presentation("Last Year's Workshop", past, true);
    }

    @Test
    void adminListsOnlyTheCurrentEditionsTalksAndWorkshops() throws Exception {
        mockMvc.perform(get("/presentations").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItems(currentTalk.getId(), currentWorkshop.getId())))
                .andExpect(jsonPath("$[*].id", not(hasItem(pastTalk.getId()))))
                .andExpect(jsonPath("$[*].id", not(hasItem(pastWorkshop.getId()))));
    }

    @Test
    void ratesCoverOnlyTheCurrentEditionsTalks() throws Exception {
        rateRepository.save(new Rate().setValue(RateValue.AWESOME).setReviewerToken("A").setPresentation(currentTalk));
        rateRepository.save(new Rate().setValue(RateValue.AWESOME).setReviewerToken("A").setPresentation(pastTalk));

        mockMvc.perform(post("/ratings").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].presentationId", hasItem(currentTalk.getId())))
                .andExpect(jsonPath("$[*].presentationId", not(hasItem(pastTalk.getId()))));
    }

    private Presentation presentation(String title, Edition edition, boolean workshop) {
        return presentationRepository.save(new Presentation()
                .setTitle(title)
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("easy")
                .setLanguage("en")
                .setWorkshop(workshop)
                .setEdition(edition));
    }
}
