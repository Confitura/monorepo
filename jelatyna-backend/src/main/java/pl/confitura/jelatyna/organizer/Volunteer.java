package pl.confitura.jelatyna.organizer;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "volunteer")
@Data
public class Volunteer extends Person{

    public String getPhoto() {
        return "http://c4p.confitura.pl/files/volunteer/" + getId() + ".jpg";
    }
}
