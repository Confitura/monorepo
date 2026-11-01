package pl.confitura.jelatyna.conference.presentation.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.confitura.jelatyna.identity.user.web.SecurityHelper.user;

import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.identity.user.web.SecurityHelper;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationData;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationRepository;
import pl.confitura.jelatyna.conference.registration.domain.VoucherService;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

// The co-speaker dialog lists a talk's speakers by name and email and removes one by id,
// so that is all the co-speaker endpoints answer with, whatever else the profile holds.
@Transactional
class CospeakersTest extends BaseIntegrationTest {

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private VoucherService voucherService;

    private User speaker;
    private User coSpeaker;

    @BeforeEach
    void speakers() {
        speaker = userRepository.save(new User().setName("Speaker").setEmail("speaker@cospeakers.test"));
        coSpeaker = userRepository.save(new User().setName("Co-speaker").setEmail("co-speaker@cospeakers.test")
                .setBio("Bio").setTwitter("twitter").setGithub("github").setWww("https://co-speaker.test")
                .setAdmin(true).setVolunteer(true).setPrivacyPolicyAccepted(true).setParticipationDataId(ticket().getId()));
    }

    @Test
    void theCospeakerListHasOnlyEachSpeakersIdNameAndEmail() throws Exception {
        Presentation talk = talk(speaker, coSpeaker);

        String body = mockMvc.perform(get("/presentations/" + talk.getId() + "/cospeakers").with(user(speaker)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Map<String, Object>> speakers = JsonPath.read(body, "$");
        assertThat(speakers).containsExactlyInAnyOrder(
                Map.of("id", speaker.getId(), "name", "Speaker", "email", "speaker@cospeakers.test"),
                Map.of("id", coSpeaker.getId(), "name", "Co-speaker", "email", "co-speaker@cospeakers.test"));
    }

    @Test
    void addingACospeakerAnswersWithOnlyTheirIdNameAndEmail() throws Exception {
        Presentation talk = talk(speaker);

        String body = mockMvc.perform(post("/presentations/" + talk.getId() + "/cospeakers/co-speaker@cospeakers.test")
                        .with(user(speaker)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> added = JsonPath.read(body, "$");
        assertThat(added).isEqualTo(Map.of("id", coSpeaker.getId(), "name", "Co-speaker", "email", "co-speaker@cospeakers.test"));
    }

    @Test
    void addingAnUnknownEmailOrAnAlreadyAddedSpeakerIsRefused() throws Exception {
        Presentation talk = talk(speaker, coSpeaker);
        String coSpeakers = "/presentations/" + talk.getId() + "/cospeakers/";

        mockMvc.perform(post(coSpeakers + "nobody@cospeakers.test").with(user(speaker)))
                .andExpect(status().isNotFound());
        mockMvc.perform(post(coSpeakers + "speaker@cospeakers.test").with(user(speaker)))
                .andExpect(status().isConflict())
                .andExpect(content().string("You cannot add yourself as a speaker!"));
        mockMvc.perform(post(coSpeakers + "co-speaker@cospeakers.test").with(user(speaker)))
                .andExpect(status().isConflict())
                .andExpect(content().string("This speaker is already added to this presentation"));
    }

    private Presentation talk(User... speakers) {
        Presentation talk = new Presentation()
                .setTitle("Talk")
                .setShortDescription("short")
                .setDescription("description")
                .setLevel("beginner")
                .setLanguage("Polish")
                .setEdition(editionRepository.findCurrent());
        for (User it : speakers) {
            talk.setSpeaker(it);
        }
        return presentationRepository.save(talk);
    }

    private ParticipationData ticket() {
        // Only an admin issues vouchers.
        SecurityHelper.asAdmin();
        ParticipationData ticket = participationRepository.save(new ParticipationData()
                .setVoucher(voucherService.generateVoucher("")).setGender("female").setSize("M"));
        SecurityHelper.cleanSecurity();
        return ticket;
    }
}
