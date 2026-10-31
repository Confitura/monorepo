package pl.confitura.jelatyna.conference.edition.web;

import java.time.Instant;
import java.time.LocalDate;

/** Payload for a new or updated Edition; the C4P period is optional, and an update must repeat the year. */
public record EditionRequest(
        Integer year,
        String name,
        Instant conferenceStart,
        LocalDate lastDay,
        Instant c4pStart,
        Instant c4pEnd) {
}
