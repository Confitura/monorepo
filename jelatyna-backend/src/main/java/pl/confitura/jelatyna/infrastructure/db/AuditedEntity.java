package pl.confitura.jelatyna.infrastructure.db;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.Instant;


/* after upgrading to spring data 2.1.x it can be changed to @Embeddable */
@Data
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AuditedEntity {
    @CreatedBy        private String createdBy;
    @CreatedDate      private Instant createdDate;
    @LastModifiedBy   private String lastModifiedBy;
    @LastModifiedDate private Instant lastModifiedDate;
}
