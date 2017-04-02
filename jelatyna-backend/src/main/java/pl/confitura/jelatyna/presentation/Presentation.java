package pl.confitura.jelatyna.presentation;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.confitura.jelatyna.user.User;

@Entity
@Data
@EqualsAndHashCode(exclude = "speaker")
public class Presentation {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;
    private String title;
    private String shortDescription;
    private String description;
    private String level;
    private String language;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();
    @ManyToOne(optional = false)
    private User speaker;


}
