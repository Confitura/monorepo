package pl.confitura.jelatyna.page;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "simple_content")
@Data
public class Page {
    @Id
    @Column(name = "title")
    private String id;

    @Column(columnDefinition = "TEXT")
    private String content;

}
