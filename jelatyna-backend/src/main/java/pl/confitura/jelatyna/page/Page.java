package pl.confitura.jelatyna.page;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
