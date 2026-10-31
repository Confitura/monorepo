package pl.confitura.jelatyna.conference.edition.web;

import java.time.Instant;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;

@Component
@RequiredArgsConstructor
class ConferenceInfoContributor implements InfoContributor {
    private final EditionRepository editionRepository;


    @Override
    public void contribute(Info.Builder builder) {
        Edition current = editionRepository.findCurrent();
        builder.withDetail("c4p", new C4p(current.getC4pStart(), current.getC4pEnd(), current.isC4pOpen()));
    }

    private record C4p(Instant start, Instant end, boolean open) {
    }
}
