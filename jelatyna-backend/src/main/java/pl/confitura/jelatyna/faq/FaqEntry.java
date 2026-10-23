package pl.confitura.jelatyna.faq;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;

/** A single FAQ question managed independently and grouped by its {@link FaqCategory}. */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
public class FaqEntry extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Nullable at the DB level (the column was added to existing rows by the one-off
    // value→entity backfill, see ADR-0001); always set for entries the app creates.
    @ManyToOne
    @JoinColumn(name = "category_id")
    private FaqCategory category;

    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    private int displayOrder;

    private boolean published;
}
