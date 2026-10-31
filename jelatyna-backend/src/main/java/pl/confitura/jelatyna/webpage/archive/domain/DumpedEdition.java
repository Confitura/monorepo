package pl.confitura.jelatyna.webpage.archive.domain;

import pl.confitura.jelatyna.edition.Edition;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public record DumpedEdition(
        int year,
        String name,
        OffsetDateTime conferenceStart,
        LocalDate lastDay) {

    // The conference's local time, so the start reads as it does on the venue's clock.
    private static final ZoneId CONFERENCE_ZONE = ZoneId.of("Europe/Warsaw");

    public static DumpedEdition from(Edition edition) {
        return new DumpedEdition(
                edition.getYear(),
                edition.getName(),
                edition.getConferenceStart().atZone(CONFERENCE_ZONE).toOffsetDateTime(),
                edition.getLastDay());
    }
}
