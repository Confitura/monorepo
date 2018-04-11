package pl.confitura.jelatyna.organizer;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "admin")
@Data
@EqualsAndHashCode(callSuper = true)
public class Organizer extends Person {
    private String filename;

    public String getPhoto() {
        return "https://2017.confitura.pl/files/kapitula/" + getId() + ".jpg";
    }
}
