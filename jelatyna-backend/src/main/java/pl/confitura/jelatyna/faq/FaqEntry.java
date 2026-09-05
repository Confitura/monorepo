package pl.confitura.jelatyna.faq;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.infrastructure.db.AuditedEntity;

/** A single FAQ question managed independently and grouped by a category string. */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
public class FaqEntry extends AuditedEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String category;

    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    private int displayOrder;

    private boolean published;
}
