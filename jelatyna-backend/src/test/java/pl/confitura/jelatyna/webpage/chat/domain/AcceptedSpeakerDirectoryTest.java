package pl.confitura.jelatyna.webpage.chat.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayDeque;
import java.util.Deque;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

// The names the chat rejoins: each speaker of an accepted talk, by id.
class AcceptedSpeakerDirectoryTest extends BaseIntegrationTest {

    @Autowired
    private SpeakerDirectory speakerDirectory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate txTemplate;

    // Newest first, so a talk goes before its speaker.
    private final Deque<Object> created = new ArrayDeque<>();

    @AfterEach
    void removeWhatTheTestCreated() {
        txTemplate.executeWithoutResult(status -> created.forEach(entity -> entityManager.remove(entityManager.merge(entity))));
    }

    @Test
    void namesTheSpeakersOfAcceptedTalksOnly() {
        User accepted = speakerOfATalk("Accepted Speaker", true);
        User notAccepted = speakerOfATalk("Not Accepted Speaker", false);

        assertThat(speakerDirectory.idToName())
                .containsEntry(accepted.getId(), "Accepted Speaker")
                .doesNotContainKey(notAccepted.getId());
    }

    private User speakerOfATalk(String name, boolean accepted) {
        User speaker = userRepository.save(new User().setName(name));
        created.push(speaker);
        Presentation talk = new Presentation().setTitle("Talk").setShortDescription("short").setDescription("long")
                .setLevel("beginner").setLanguage("Polish").setEdition(editionRepository.findCurrent());
        talk.setSpeaker(speaker);
        talk.setAccepted(accepted);
        created.push(presentationRepository.save(talk));
        return speaker;
    }
}
