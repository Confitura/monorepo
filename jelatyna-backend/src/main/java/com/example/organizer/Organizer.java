package com.example.organizer;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "admin")
@Data
public class Organizer extends Person {
    private String filename;

    public String getPhoto() {
        return "http://c4p.confitura.pl/files/kapitula/" + getId() + ".jpg";
    }
}
