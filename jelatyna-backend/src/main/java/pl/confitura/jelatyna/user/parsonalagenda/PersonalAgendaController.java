package pl.confitura.jelatyna.user.parsonalagenda;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.agenda.AgendaEntry;
import pl.confitura.jelatyna.agenda.AgendaRepository;
import pl.confitura.jelatyna.agenda.InlineAgenda;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
public class PersonalAgendaController {

    private final AgendaRepository agendaRepository;
    private final ProjectionFactory projectionFactory;
    private final PersonalAgendaRepository personalAgendaRepository;

    @PostMapping("/users/{userId}/personalAgenda")
    public ResponseEntity<?> addEntry(@RequestBody AddAgendaEntryRequest entryRequest, @PathVariable String userId) {
        AgendaEntry agendaEntry = agendaRepository.findById(entryRequest.agendaEntryId);
        removeCurrentEntryWithSameTimeSlot(userId, agendaEntry);
        addNewEntry(userId, agendaEntry);
        return ResponseEntity.status(OK).build();
    }

    private void addNewEntry(String userId, AgendaEntry agendaEntry) {
        PersonalAgendaEntry personalAgendaEntry = new PersonalAgendaEntry(userId, agendaEntry);
        personalAgendaRepository.save(personalAgendaEntry);
    }

    private void removeCurrentEntryWithSameTimeSlot(String userId, AgendaEntry agendaEntry) {
        personalAgendaRepository.deleteByUserIdAndAgendaEntryTimeSlot(userId, agendaEntry.getTimeSlot());
    }

    @GetMapping("/users/{userId}/personalAgenda")
    public ResponseEntity<?> getAgenda(@PathVariable String userId) {
        List<AgendaEntry> allRoomsTimeSlotEntries = agendaRepository.findEntriesForAllRooms();
        Set<AgendaEntry> personalAgenda = personalAgendaRepository.findPersonalAgenda(userId);
        Stream<AgendaEntry> fullAgenda = concat(allRoomsTimeSlotEntries, personalAgenda);
        List<InlineAgenda> agendaWithInlinedResources = fullAgenda
                .map(it -> projectionFactory.createProjection(InlineAgenda.class, it))
                .collect(toList());
        return ResponseEntity.ok(new Resources<>(agendaWithInlinedResources));
    }

    private Stream<AgendaEntry> concat(List<AgendaEntry> allRoomsTimeSlotEntries, Set<AgendaEntry> personalAgenda) {
        List<AgendaEntry> fullAgenda = new ArrayList<>(allRoomsTimeSlotEntries);
        fullAgenda.addAll(personalAgenda);
        return fullAgenda
                .stream()
                .sorted(comparing(AgendaEntry::getTimeSlotOrder))
                .distinct();
    }

    @Data
    private static class AddAgendaEntryRequest {
        private String agendaEntryId;
    }
}
