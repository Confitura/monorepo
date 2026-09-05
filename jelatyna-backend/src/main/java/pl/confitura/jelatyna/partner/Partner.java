package pl.confitura.jelatyna.partner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
public class Partner {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;
    /** URL slug set explicitly by an admin (e.g. "xtb"); used in partner page URLs. */
    private String slug;
    @Column(columnDefinition = "varchar(10000)")
    private String description;
    private String name;
    private String www;
    private String type;
    private String logo;
    private String orientation;
    private boolean published;
}
