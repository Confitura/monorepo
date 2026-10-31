package pl.confitura.jelatyna.conference.edition.web;

import java.time.Instant;
import java.time.LocalDate;

import pl.confitura.jelatyna.conference.edition.domain.Edition;

/** View of an Edition, for admins and for the speaker homepage's Edition picker. */
public record EditionDto(
        String id,
        int year,
        String name,
        Instant conferenceStart,
        LocalDate lastDay,
        Instant c4pStart,
        Instant c4pEnd,
        boolean current) {

    public static EditionDto from(Edition e) {
        return new EditionDto(e.getId(), e.getYear(), e.getName(), e.getConferenceStart(), e.getLastDay(),
                e.getC4pStart(), e.getC4pEnd(), e.isCurrent());
    }
}
