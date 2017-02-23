package pl.confitura.jelatyna.organizer;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.experimental.Accessors;

@MappedSuperclass
@Data
@Accessors(chain = true)
public abstract class Person {
    @Id
    private Long id;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    private String bio;
    private String twitter;
    private String mail;
    @Column(name = "webpage")
    private String webPage;

    public abstract String getPhoto();

}
