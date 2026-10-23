package pl.confitura.jelatyna.edition;

import java.time.Instant;
import java.time.LocalDate;

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
