package pl.confitura.jelatyna.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.ConferenceConfigurationProperties;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.user.UserFacade;
import pl.confitura.jelatyna.user.dto.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Component
public class PresentationFacade {

    private final PresentationRepository repository;
    private final UserFacade userFacade;
    private final ConferenceConfigurationProperties conferenceConfiguration;
    private final Security security;


    public void accept(String presentationId) {
        Presentation presentation = this.repository.findById(presentationId);
        presentation.setAccepted(true);
        Set<Speaker> speakers = presentation.getSpeakers();
        for (Speaker speaker : speakers) {
            userFacade.markSpeaker(speaker.getId(), true);
        }
    }

    public void unaccept(String presentationId) {
        Presentation presentation = this.repository.findById(presentationId);
        presentation.setAccepted(false);
        repository.save(presentation);
        Set<Speaker> speakers = presentation.getSpeakers();
        for (Speaker speaker : speakers) {
            Long accepted = repository.countAcceptedWithSpeaker(speaker);
            boolean isSpeaker = accepted > 0;
            userFacade.markSpeaker(speaker.getId(), isSpeaker);
        }
    }

    public Set<Speaker> getSpeakers(String presentationId) {
        return repository.findById(presentationId).getSpeakers();
    }

    public RemoveSpeakerStatus removeSpeakerFromPresentation(String presentationId, String usetId) {
        Presentation presentation = this.repository.findById(presentationId);
        if (presentation.getSpeakers().size() == 1) {
            return RemoveSpeakerStatus.REMOVING_LAST_SPEAKER;
        }
        presentation.setSpeakers(removeSpeakerById(usetId, presentation.getSpeakers()));
        repository.save(presentation);
        return RemoveSpeakerStatus.OK;

    }

    private Set<Speaker> removeSpeakerById(String id, Set<Speaker> cospeakers) {
        return cospeakers.stream().filter(it -> !it.getId().equalsIgnoreCase(id)).collect(Collectors.toSet());
    }

    public AddSpeakerStatus addSpeaker(String presentationId, String speakersEmail) {
        User user = this.userFacade.findByEmail(speakersEmail);
        if (user == null) {
            return AddSpeakerStatus.SPEAKER_NOT_EXISTS;
        }

        Presentation presentation = this.repository.findById(presentationId);
        if (presentation.isOwnedBy(speakersEmail)) {
            return AddSpeakerStatus.SPEAKER_ALREADY_ADDED;
        }
        presentation.getSpeakers().add(Speaker.fromUser(user));
        return AddSpeakerStatus.OK;
    }

    public SubmitPresentationStatus submitPresentation(pl.confitura.jelatyna.presentation.dto.Presentation presentationDto, String userId) {
        Presentation presentation = Presentation.fromDto(presentationDto);
        if (presentation.isNew() && !canCreatePresentation()) {
            return SubmitPresentationStatus.CALL_FOR_PAPERS_CLOSED;
        }
        User speaker = userFacade.findById(userId);
        presentation.setSpeaker(Speaker.fromUser(speaker));
        retainStatus(presentation);
        repository.save(presentation);
        return SubmitPresentationStatus.OK;
    }

    private boolean canCreatePresentation() {
        return conferenceConfiguration.getC4p().isEnabled() || security.isAdmin();
    }

    private void retainStatus(Presentation presentation) {
        if (!presentation.isNew()) {
            Presentation saved = repository.findById(presentation.getId());
            presentation.setStatus(saved.getStatus());
        }
    }

    public pl.confitura.jelatyna.presentation.dto.Presentation findById(String presentationId) {
        return repository.findById(presentationId).toDto();
    }

    //todo refactor to remove
    @Deprecated
    public pl.confitura.jelatyna.presentation.dto.Presentation save(pl.confitura.jelatyna.presentation.dto.Presentation presentation) {
        // todo fetch presentation and update fields
        return repository.save(Presentation.fromDto(presentation)).toDto();
    }

    public List<pl.confitura.jelatyna.presentation.dto.Presentation> findAllForV4p() {
        return repository.findAllForV4p().stream()
                .map(Presentation::toDto)
                .collect(toList());
    }

    public enum RemoveSpeakerStatus {
        OK,
        REMOVING_LAST_SPEAKER
    }

    public enum AddSpeakerStatus {
        OK,
        SPEAKER_NOT_EXISTS,
        SPEAKER_ALREADY_ADDED
    }

    public enum SubmitPresentationStatus {
        OK,
        CALL_FOR_PAPERS_CLOSED
    }
}


