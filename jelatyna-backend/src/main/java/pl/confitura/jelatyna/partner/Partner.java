package pl.confitura.jelatyna.partner;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Table(name = "sponsor")
@Data
public class Partner {
    @Id
    private Long id;
    private String description;
    private String name;
    private String webpage;
    private String type;
    @Transient
    private String logo;

    public String getLogo(){
        return "http://c4p.confitura.pl//files/sponsors/" + id + ".jpg";
    }
}
