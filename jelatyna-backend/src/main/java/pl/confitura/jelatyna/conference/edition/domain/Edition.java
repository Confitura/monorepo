package pl.confitura.jelatyna.conference.edition.domain;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


import lombok.Data;
import lombok.experimental.Accessors;

/** One yearly Confitura conference; exactly one Edition is the Current Edition (ADR-0004). */
@Entity
@Data
@Accessors(chain = true)
public class Edition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "varchar(100)")
    private String id;

    // Fixed once created: ADR-0004 names the Edition's dump folder after it.
    @Column(updatable = false)
    private int year;

    @Column(columnDefinition = "text")
    private String name;

    private Instant conferenceStart;

    private LocalDate lastDay;

    // The C4P period; unset means closed.
    private Instant c4pStart;
    private Instant c4pEnd;

    private boolean current;

    public boolean isC4pOpen() {
        Instant now = Instant.now();
        return c4pStart != null && c4pEnd != null && now.isAfter(c4pStart) && now.isBefore(c4pEnd);
    }
}
