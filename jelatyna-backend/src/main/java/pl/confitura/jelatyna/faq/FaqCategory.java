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

/**
 * A dictionary entry that groups {@link FaqEntry} questions. Owns its own visibility
 * ({@code published}) and ordering ({@code displayOrder}), independent of the entries in it.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
public class FaqCategory extends AuditedEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(unique = true)
    private String name;

    private int displayOrder;

    private boolean published = true;
}
