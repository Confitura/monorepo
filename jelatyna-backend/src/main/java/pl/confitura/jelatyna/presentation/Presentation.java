package pl.confitura.jelatyna.presentation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.confitura.jelatyna.user.User;

@Entity
@Data
@ToString(exclude = "speaker")
@EqualsAndHashCode(exclude = "speaker")
public class Presentation {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;
    @NotBlank
    private String title;
    @NotBlank
    @Column(length = 300)
    private String shortDescription;
    @NotBlank()
    @Column(length = 1000)
    private String description;
    @NotBlank
    private String level;
    @NotBlank
    private String language;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();
    @ManyToOne(optional = false)
    @NotNull
    private User speaker;
    @ManyToMany
    @NotNull
    private List<User> cospeakers = new ArrayList<>();
    private String status;
}
