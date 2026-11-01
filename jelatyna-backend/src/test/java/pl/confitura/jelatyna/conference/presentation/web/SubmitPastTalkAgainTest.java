package pl.confitura.jelatyna.conference.presentation.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.identity.user.web.SecurityHelper.admin;
import static pl.confitura.jelatyna.identity.user.web.SecurityHelper.user;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.identity.user.web.SecurityHelper;
import pl.confitura.jelatyna.conference.presentation.domain.PreSelectionStatus;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.conference.presentation.domain.Tag;
import pl.confitura.jelatyna.conference.presentation.domain.TagRepository;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

// "Submit again" copies a past Edition's talk or workshop into the Current Edition as a fresh
// submission by the speaker who resubmits it; the original stays as it was (ADR-0004).
@Transactional
class SubmitPastTalkAgainTest extends BaseIntegrationTest {

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    private User speaker;
    private User coSpeaker;
    private Edition past;
    private Tag tag;

    enum Kind {
        TALK, WORKSHOP;

        String path() {
            return this == TALK ? "presentations" : "workshops";
        }
    }

    @BeforeEach
    void aPastEdition() {
        speaker = userRepository.save(new User().setName("Speaker"));
        coSpeaker = userRepository.save(new User().setName("Co-speaker"));
        tag = tagRepository.saveAll(List.of(new Tag("submit-again", "Submit again"))).iterator().next();
        SecurityHelper.asAdmin();
        past = editionRepository.save(new Edition().setYear(2025).setName("Confitura 2025")
                .setConferenceStart(Instant.parse("2025-06-28T07:00:00Z")).setLastDay(LocalDate.of(2025, 6, 28)));
        SecurityHelper.cleanSecurity();
    }

    @ParameterizedTest
    @EnumSource
    void aSpeakerSubmitsAPastTalkAgainAsAFreshSubmissionInTheCurrentEdition(Kind kind) throws Exception {
        Presentation original = talk(past, kind);

        String copyId = idOf(submitAgain(kind, original, speaker, user(speaker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Talk")))
                .andExpect(jsonPath("$.shortDescription", is("short")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.level", is("beginner")))
                .andExpect(jsonPath("$.language", is("Polish")))
                .andExpect(jsonPath("$.tags", contains("submit-again")))
                .andExpect(jsonPath("$.status", is("reported"))));

        mockMvc.perform(get("/users/" + speaker.getId() + "/" + kind.path()).with(user(speaker)))
                .andExpect(jsonPath("$[*].id", contains(copyId)));
        mockMvc.perform(get("/presentations/" + copyId + "/cospeakers").with(user(speaker)))
                .andExpect(jsonPath("$[*].id", contains(speaker.getId())));
        Presentation copy = presentationRepository.findById(copyId);
        assertThat(copy.getPreSelectionStatus()).isEqualTo(PreSelectionStatus.NONE);
        assertThat(copy.getPreSelectionComment()).isNull();
    }

    @Test
    void aWorkshopIsSubmittedAgainWithItsWorkshopDetails() throws Exception {
        Presentation original = talk(past, Kind.WORKSHOP);

        submitAgain(Kind.WORKSHOP, original, speaker, user(speaker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFree", is(false)))
                .andExpect(jsonPath("$.expectedPrice", is(500.0)))
                .andExpect(jsonPath("$.durationInMinutes", is(180)))
                .andExpect(jsonPath("$.maxGroupSize", is(20)));
    }

    @ParameterizedTest
    @EnumSource
    void theOriginalStaysInItsEditionWithItsSpeakersAndReview(Kind kind) throws Exception {
        Presentation original = talk(past, kind);

        submitAgain(kind, original, speaker, user(speaker)).andExpect(status().isOk());

        mockMvc.perform(get("/users/" + speaker.getId() + "/" + kind.path()).param("editionId", past.getId()).with(user(speaker)))
                .andExpect(jsonPath("$[*].id", contains(original.getId())))
                .andExpect(jsonPath("$[0].status", is("accepted")));
        Presentation kept = presentationRepository.findById(original.getId());
        assertThat(kept.getSpeakers()).extracting(User::getId).containsExactlyInAnyOrder(speaker.getId(), coSpeaker.getId());
        assertThat(kept.getPreSelectionStatus()).isEqualTo(PreSelectionStatus.PRE_APPROVED);
        assertThat(kept.getPreSelectionComment()).isEqualTo("Strong talk");
    }

    @ParameterizedTest
    @EnumSource
    void onlyASpeakerOfTheTalkCanSubmitItAgain(Kind kind) throws Exception {
        Presentation original = talk(past, kind);
        User otherSpeaker = userRepository.save(new User().setName("Other Speaker"));
        RequestPostProcessor anonymous = request -> request;

        submitAgain(kind, original, speaker, user(otherSpeaker)).andExpect(status().isForbidden());
        submitAgain(kind, original, speaker, anonymous).andExpect(status().isForbidden());
        submitAgain(kind, original, otherSpeaker, user(otherSpeaker)).andExpect(status().isForbidden());
        submitAgain(kind, original, coSpeaker, user(coSpeaker)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @EnumSource
    void aTalkOfTheOtherKindIsRefused(Kind kind) throws Exception {
        Presentation original = talk(past, kind == Kind.TALK ? Kind.WORKSHOP : Kind.TALK);

        submitAgain(kind, original, speaker, user(speaker)).andExpect(status().isForbidden());
        submitAgain(kind, original, speaker, admin()).andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @EnumSource
    void onlyAnAdminCanSubmitAgainWhileTheCallForPapersIsClosed(Kind kind) throws Exception {
        SecurityHelper.asAdmin();
        editionRepository.save(editionRepository.findCurrent().setC4pStart(null).setC4pEnd(null));
        SecurityHelper.cleanSecurity();
        Presentation original = talk(past, kind);

        submitAgain(kind, original, speaker, user(speaker)).andExpect(status().isForbidden());
        submitAgain(kind, original, speaker, admin()).andExpect(status().isOk());
    }

    // The copy would duplicate a submission the speaker can still edit.
    @ParameterizedTest
    @EnumSource
    void aCurrentEditionsTalkIsNotSubmittedAgain(Kind kind) throws Exception {
        Presentation original = talk(editionRepository.findCurrent(), kind);

        submitAgain(kind, original, speaker, user(speaker)).andExpect(status().isConflict());
        submitAgain(kind, original, speaker, admin()).andExpect(status().isConflict());
    }

    private ResultActions submitAgain(Kind kind, Presentation talk, User pathUser, RequestPostProcessor signedIn)
            throws Exception {
        return mockMvc.perform(post("/users/" + pathUser.getId() + "/" + kind.path() + "/" + talk.getId() + "/resubmit")
                .with(signedIn));
    }

    private static String idOf(ResultActions result) throws Exception {
        return JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.id");
    }

    private Presentation talk(Edition edition, Kind kind) {
        Presentation talk = new Presentation()
                .setTitle("Talk")
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("beginner")
                .setLanguage("Polish")
                .setTags(new HashSet<>(Set.of(tag)))
                .setSpeakers(new HashSet<>(Set.of(speaker, coSpeaker)))
                .setStatus(Presentation.STATUS_ACCEPTED)
                .setPreSelectionStatus(PreSelectionStatus.PRE_APPROVED)
                .setPreSelectionComment("Strong talk")
                .setEdition(edition);
        if (kind == Kind.WORKSHOP) {
            talk.setWorkshop(true).setIsFree(false).setExpectedPrice(500.0).setDurationInMinutes(180).setMaxGroupSize(20);
        }
        return presentationRepository.save(talk);
    }
}
