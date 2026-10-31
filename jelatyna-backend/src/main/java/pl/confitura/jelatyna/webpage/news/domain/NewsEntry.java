package pl.confitura.jelatyna.webpage.news.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;

/** A dated announcement on the webpage's news page; public once published and its date has passed. */
@Data
@Entity
public class NewsEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "varchar(100)")
    private String id;

    @Column(columnDefinition = "text")
    private String title;

    @Column(columnDefinition = "text")
    private String slug;

    // Markdown; the entries seeded from Listmonk hold HTML, which Markdown passes through.
    @Column(columnDefinition = "text")
    private String body;

    // Both the date shown and the moment the entry goes public, so a future date schedules it.
    private Instant date;

    private boolean published;

    // Where the resources upload stored the cover image; null when the entry has none.
    @Column(columnDefinition = "text")
    private String cover;
}
