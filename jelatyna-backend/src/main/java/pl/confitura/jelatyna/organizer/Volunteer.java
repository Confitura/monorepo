package pl.confitura.jelatyna.organizer;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "volunteer")
@Data
@EqualsAndHashCode(callSuper = true)
public class Volunteer extends Person{

    public String getPhoto() {
        return "http://c4p.confitura.pl/files/volunteer/" + getId() + ".jpg";
    }
}
