package pl.confitura.jelatyna.user.parsonalagenda;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.agenda.AgendaEntry;

import javax.persistence.*;

@Entity
@Data
class PersonalAgendaEntry {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(100)")
    private String id;

    private String userId;

    @ManyToOne
    private AgendaEntry agendaEntry;

    PersonalAgendaEntry(String userId, AgendaEntry agendaEntry) {
        this.userId = userId;
        this.agendaEntry = agendaEntry;
    }
}
