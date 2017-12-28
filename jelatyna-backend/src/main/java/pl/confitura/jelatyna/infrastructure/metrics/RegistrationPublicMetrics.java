package pl.confitura.jelatyna.infrastructure.metrics;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.registration.ParticipantRepository;

import java.util.Collection;
import java.util.LinkedHashSet;

@Component
public class RegistrationPublicMetrics implements PublicMetrics {

    private final ParticipantRepository participantRepository;
    private final PresentationRepository presentationRepository;

    public RegistrationPublicMetrics(ParticipantRepository participantRepository, PresentationRepository presentationRepository) {
        this.participantRepository = participantRepository;
        this.presentationRepository = presentationRepository;
    }

    @Override
    public Collection<Metric<?>> metrics() {
        Collection<Metric<?>> metrics = new LinkedHashSet<>();
        metrics.add(new Metric<>("conference.participants.all", participantRepository.count()));
        metrics.add(new Metric<>("conference.participants.registered", participantRepository.countRegistered()));
        metrics.add(new Metric<>("conference.participants.arrived", participantRepository.countArrived()));
        metrics.add(new Metric<>("conference.presentations.all", presentationRepository.count()));
        metrics.add(new Metric<>("conference.presentations.accepted", presentationRepository.countAccepted()));
        return metrics;
    }
}
