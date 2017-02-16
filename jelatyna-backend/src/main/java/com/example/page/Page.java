package com.example.page;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "SimpleContent")
@Data
public class Page {
    @Id
    @Column(name = "title")
    private String id;
    private String content;

}
