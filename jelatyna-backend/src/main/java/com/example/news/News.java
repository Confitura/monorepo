package com.example.news;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity()
@Table(name="news")
@Data
public class News {
    @Id
    private Long id;
    private String title;
    @Column(name="creationdate")

    private LocalDateTime creationDate;
    private String description;
    private boolean published;
    @Column(name="shortdescription")
    private String shortDescription;
}
