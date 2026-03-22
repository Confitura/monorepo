package pl.confitura.jelatyna.registration.demographic;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Set;

@Data
@Entity
@Accessors(chain = true)
public class DemographicData {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String city;
    private String experience;
    private String role;
    private String mealOption;

    @ElementCollection
    private Set<String> technologies;

}
