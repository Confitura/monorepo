package pl.confitura.jelatyna.conference.presentation.web;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.identity.user.web.SecurityHelper.admin;
import static pl.confitura.jelatyna.identity.user.web.SecurityHelper.user;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRequest;
import pl.confitura.jelatyna.conference.presentation.domain.WorkshopRequest;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.identity.user.web.SecurityHelper;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

// 403, not 401, once the call for papers closes: the admin app treats a 401 as a rejected token
// and signs the user out.
class SpeakerPresentationControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    private Edition before;

    @BeforeEach
    void rememberTheCallForPapers() {
        before = editionRepository.findCurrent();
    }

    @AfterEach
    void restoreTheCallForPapers() {
        setCallForPapers(before.getC4pStart(), before.getC4pEnd());
    }

    @Test
    void addingATalkWhileTheCallForPapersIsOpenIsAllowed() throws Exception {
        setCallForPapers(Instant.now().minus(1, DAYS), Instant.now().plus(1, DAYS));
        User speaker = userRepository.save(new User().setName("Speaker"));

        addTalk(speaker, user(speaker)).andExpect(status().isOk());
    }

    @Test
    void addingATalkAfterTheCallForPapersClosedIsForbidden() throws Exception {
        setCallForPapers(Instant.now().minus(2, DAYS), Instant.now().minus(1, DAYS));
        User speaker = userRepository.save(new User().setName("Late Speaker"));

        addTalk(speaker, user(speaker)).andExpect(status().isForbidden());
    }

    @Test
    void addingATalkWithoutACallForPapersIsForbidden() throws Exception {
        setCallForPapers(null, null);
        User speaker = userRepository.save(new User().setName("Early Speaker"));

        addTalk(speaker, user(speaker)).andExpect(status().isForbidden());
    }

    @Test
    void anAdminCanAddATalkAfterTheCallForPapersClosed() throws Exception {
        setCallForPapers(Instant.now().minus(2, DAYS), Instant.now().minus(1, DAYS));
        User speaker = userRepository.save(new User().setName("Invited Speaker"));

        addTalk(speaker, admin()).andExpect(status().isOk());
    }

    @Test
    void addingAWorkshopAfterTheCallForPapersClosedIsForbidden() throws Exception {
        setCallForPapers(Instant.now().minus(2, DAYS), Instant.now().minus(1, DAYS));
        User speaker = userRepository.save(new User().setName("Late Speaker"));

        addWorkshop(speaker).andExpect(status().isForbidden());
    }

    @Test
    void aTalkASpeakerAddsIsInTheCurrentEdition() throws Exception {
        setCallForPapers(Instant.now().minus(1, DAYS), Instant.now().plus(1, DAYS));
        User speaker = userRepository.save(new User().setName("Speaker"));

        addTalk(speaker, user(speaker)).andExpect(status().isOk());

        assertThat(editionIdsOfTheTalksOf(speaker)).containsExactly(editionRepository.findCurrent().getId());
    }

    @Test
    void aTalkAnAdminAddsIsInTheCurrentEdition() throws Exception {
        setCallForPapers(Instant.now().minus(2, DAYS), Instant.now().minus(1, DAYS));
        User speaker = userRepository.save(new User().setName("Invited Speaker"));

        addTalk(speaker, admin()).andExpect(status().isOk());

        assertThat(editionIdsOfTheTalksOf(speaker)).containsExactly(editionRepository.findCurrent().getId());
    }

    @Test
    void aWorkshopASpeakerAddsIsInTheCurrentEdition() throws Exception {
        setCallForPapers(Instant.now().minus(1, DAYS), Instant.now().plus(1, DAYS));
        User speaker = userRepository.save(new User().setName("Workshop Speaker"));

        addWorkshop(speaker).andExpect(status().isOk());

        assertThat(editionIdsOfTheTalksOf(speaker)).containsExactly(editionRepository.findCurrent().getId());
    }

    private ResultActions addTalk(User speaker, RequestPostProcessor signedIn) throws Exception {
        return mockMvc.perform(post("/users/" + speaker.getId() + "/presentations")
                .with(signedIn)
                .contentType(APPLICATION_JSON)
                .content(json(new PresentationRequest("Talk", "short", "long", "beginner", "Polish", new String[]{}))));
    }

    private ResultActions addWorkshop(User speaker) throws Exception {
        return mockMvc.perform(post("/users/" + speaker.getId() + "/workshops")
                .with(user(speaker))
                .contentType(APPLICATION_JSON)
                .content(json(new WorkshopRequest("Workshop", "short", "long", "beginner", "Polish", new String[]{}, true, 0.0, 180, 20))));
    }

    private List<String> editionIdsOfTheTalksOf(User speaker) {
        return presentationRepository.findBySpeakersContains(speaker).stream()
                .map(talk -> talk.getEdition().getId())
                .toList();
    }

    private void setCallForPapers(Instant start, Instant end) {
        SecurityHelper.asAdmin();
        editionRepository.save(editionRepository.findCurrent().setC4pStart(start).setC4pEnd(end));
        SecurityHelper.cleanSecurity();
    }
}
