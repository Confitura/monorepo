package pl.confitura.jelatyna.webpage.partner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


import lombok.Data;

@Entity
@Data
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "varchar(100)")
    private String id;
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
