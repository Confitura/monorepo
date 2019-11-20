package pl.confitura.jelatyna.user.parsonalagenda;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import pl.confitura.jelatyna.agenda.AgendaEntry;
import pl.confitura.jelatyna.agenda.TimeSlot;

import java.util.Set;

interface PersonalAgendaRepository extends Repository<PersonalAgendaEntry, String> {
    void save(PersonalAgendaEntry personalAgendaEntry);

    void deleteByUserIdAndAgendaEntryTimeSlot(String userId, TimeSlot timeSlot);

    @Query("select pae.agendaEntry from PersonalAgendaEntry pae where pae.userId = ?1 ")
    Set<AgendaEntry> findPersonalAgenda(String id);
}
