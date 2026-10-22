package pl.confitura.jelatyna.edition;

import java.time.Instant;
import java.time.LocalDate;

/** Payload for a new Edition; the C4P period is optional. */
public record EditionRequest(
        Integer year,
        String name,
        Instant conferenceStart,
        LocalDate lastDay,
        Instant c4pStart,
        Instant c4pEnd) {
}
