package pl.confitura.jelatyna.partner;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
    @Column(columnDefinition = "varchar(10000)")
    private String description;
    private String name;
    private String www;
    private String type;
    private String logo;
    private boolean published;
}
