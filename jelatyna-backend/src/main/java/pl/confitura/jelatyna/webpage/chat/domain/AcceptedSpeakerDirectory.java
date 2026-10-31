package pl.confitura.jelatyna.webpage.chat.domain;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.conference.presentation.domain.PublicSpeaker;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;

import java.util.HashMap;
import java.util.Map;

/** Speaker directory backed by the existing accepted-speaker query. */
@Component
public class AcceptedSpeakerDirectory implements SpeakerDirectory {

    private final PresentationRepository presentationRepository;

    public AcceptedSpeakerDirectory(PresentationRepository presentationRepository) {
        this.presentationRepository = presentationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> idToName() {
        Map<String, String> map = new HashMap<>();
        for (PublicSpeaker speaker : PublicSpeaker.of(presentationRepository.findAccepted())) {
            if (speaker.id() != null && speaker.name() != null && !speaker.name().isBlank()) {
                map.put(speaker.id(), speaker.name());
            }
        }
        return map;
    }
}
