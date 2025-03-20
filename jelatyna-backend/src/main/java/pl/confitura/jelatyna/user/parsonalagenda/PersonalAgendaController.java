package pl.confitura.jelatyna.user.parsonalagenda;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.confitura.jelatyna.agenda.AgendaEntry;
import pl.confitura.jelatyna.agenda.AgendaRepository;
import pl.confitura.jelatyna.agenda.InlineAgenda;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@RepositoryRestController
@AllArgsConstructor
public class PersonalAgendaController {

    private final AgendaRepository agendaRepository;
    private final ProjectionFactory projectionFactory;
    private final UserRepository userRepository;

    @PostMapping("/users/{userId}/personalAgenda")
    public ResponseEntity<?> addEntry(@RequestBody AddAgendaEntryRequest entryRequest, @PathVariable User userId) {
        AgendaEntry agendaEntry = agendaRepository.findById(entryRequest.agendaEntryId);
        removeCurrentEntryWithSameTimeSlot(userId, agendaEntry);
        addNewEntry(userId, agendaEntry);
        return ResponseEntity.status(OK).build();
    }

    private void addNewEntry(User user, AgendaEntry agendaEntry) {
        user.addToPersonalAgenda(agendaEntry);
        userRepository.save(user);
    }

    private void removeCurrentEntryWithSameTimeSlot(User user, AgendaEntry agendaEntry) {
        if (user.personalAgendaContainsTimeSlot(agendaEntry.getTimeSlot())) {
            user.getFromPersonalAgendaWithTimeSlot(agendaEntry.getTimeSlot())
                    .ifPresent(user::removeFromPersonalAgenda);
        }
    }

    @GetMapping("/users/{userId}/personalAgenda")
    public ResponseEntity<?> getAgenda(@PathVariable User userId) {
        List<AgendaEntry> allRoomsTimeSlotEntries = agendaRepository.findEntriesForAllRooms();
        Set<AgendaEntry> personalAgenda = userId.getPersonalAgenda();
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
    public static class AddAgendaEntryRequest {
        private String agendaEntryId;
    }
}
