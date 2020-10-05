package pl.confitura.jelatyna.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.confitura.jelatyna.ConferenceConfigurationProperties;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.user.UserFacade;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class PresentationFacade {

    private final PresentationRepository repository;
    private final UserFacade userFacade;
    private final ConferenceConfigurationProperties conferenceConfiguration;
    private final Security security;

    @PreAuthorize("@security.isAdmin()")
    public void accept(String presentationId) {

//        Presentation presentation = this.repository.findById(presentationId);
//        TODO
//        presentation.getSpeakers().forEach(speaker -> {
//            userFacade.markSpeaker(speaker.getId(), true);
//        });
    }

    @PreAuthorize("@security.isAdmin()")
    public void unaccept(String presentationId) {
//        TODO
//        Presentation presentation = this.repository.findById(presentationId);
//        presentation.setAccepted(false);
//        repository.save(presentation);
//        presentation.getSpeakers().forEach(speaker -> {
//            Long accepted = repository.countAcceptedWithSpeaker(speaker);
//            boolean isSpeaker = accepted > 0;
//            userFacade.markSpeaker(speaker.getId(), isSpeaker);
//        });
    }

    //todo    @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    public Presentation addSpeakerByEmail(String presentationId, String email) {
//TODO
//        FullUserDto user = this.userFacade.findByEmail(email);
//        if (user == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Presentation presentation = this.repository.findById(presentationId);
//        if (presentation.isOwnedBy(email)) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("This speaker is already added to this presentation");
//        }
//        presentation.getSpeakers().add(SpeakerEntity.fromUser(user));
        return null;
    }

    //todo @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    public Set<SpeakerEntity> getSpeakers(String presentationId) {
        //TODO
//        Set<SpeakerEntity> cospeakers = this.repository.findById(presentationId).getSpeakers();
        return null;
    }

    //todo  @PreAuthorize("@security.presentationOwnedByUser(#presentationId) || @security.isAdmin()")
    public void removeSpeaker(String presentationId, String id) {
//        TODO
//        Presentation presentation = this.repository.findById(presentationId);
//        if (presentation.getSpeakers().size() == 1) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Presentation needs at least one speaker!");
//        }
//        presentation.setSpeakers(removeCospeakerById(id, presentation.getSpeakers()));
//        repository.save(presentation);
    }

    public Presentation savePresentation(Presentation presentation, String userId) {
//        TODO
//        if (presentation.isNew() && !canCreatePresentation()) {
//            return ResponseEntity.status(UNAUTHORIZED).build();
//        }
//        FullUserDto speaker = userFacade.findById(userId);
//        presentation.setSpeaker(SpeakerEntity.fromUser(speaker));
//        retainStatus(presentation);
        PresentationEntity entity = presentation.toEntity();
        PresentationEntity saved = repository.save(entity);
        return Presentation.from(saved);

    }

    public Iterable<Presentation> findAllForV4p() {
        Stream<PresentationEntity> allForV4p = repository.findAllForV4p();
        return allForV4p.map(Presentation::from).collect(Collectors.toList());
    }

    public boolean isPresentationOwnByUser(String presentationId, String userId) {
        return repository.isPresentationOwnByUser(presentationId, userId);
    }

    public Presentation findById(String presentationId) {
        PresentationEntity entity = repository.findById(presentationId);
        return Presentation.from(entity);
    }
//
//    private boolean canCreatePresentation() {
//        return conferenceConfiguration.getC4p().isEnabled() || security.isAdmin();
//    }
//
//    private void retainStatus(Presentation presentation) {
//        if (!presentation.isNew()) {
//            Presentation saved = repository.findById(presentation.getId());
//            presentation.setStatus(saved.getStatus());
//        }
//    }
}
