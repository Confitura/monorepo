package pl.confitura.jelatyna.conference.edition.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;

class ConferenceInfoContributorTest extends BaseIntegrationTest {

    @Autowired
    private EditionRepository editionRepository;

    // fake-db keeps the Current Edition's C4P open.
    @Test
    void reportsTheCurrentEditionsCallForPapers() throws Exception {
        Edition current = editionRepository.findCurrent();

        mockMvc.perform(get("/actuator/info"))
                .andExpect(jsonPath("$.c4p.start").value(current.getC4pStart().toString()))
                .andExpect(jsonPath("$.c4p.end").value(current.getC4pEnd().toString()))
                .andExpect(jsonPath("$.c4p.open").value(true));
    }
}
