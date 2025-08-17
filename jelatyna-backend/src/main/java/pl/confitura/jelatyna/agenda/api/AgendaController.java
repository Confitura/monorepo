package pl.confitura.jelatyna.agenda.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.agenda.*;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaRepository agendaRepository;
    private final DayRepository dayRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;
    private final AgendaService agendaService;
    private final PresentationRepository presentationRepository;


    @GetMapping("/{dayId}/entries")
    public ResponseEntity<List<InlineAgendaEntry>> getAgendaEntriesByDay(@PathVariable String dayId) {
        Day day = dayRepository.findById(dayId);
        if (day == null) {
            return ResponseEntity.notFound().build();
        }
        var result = agendaRepository.findByTimeSlotIdDayId(day.getId()).stream()
                .map(InlineAgendaEntry::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/entries/{id}")
    public ResponseEntity<InlineAgendaEntry> getAgendaEntryById(@PathVariable String id) {
        AgendaEntry agendaEntry = agendaRepository.findById(id);
        if (agendaEntry == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(InlineAgendaEntry.from(agendaEntry));
    }

    @PostMapping("/entries")
    public ResponseEntity<InlineAgendaEntry> saveAgendaEntry(@RequestBody AssignAgendaEntryRequest request) {
        var agendaEntry = agendaService.createAgendaEntry(
                request.dayId(),
                request.timeSlotIndex(),
                request.roomId().orElse(null),
                request.label(),
                request.presentationId()
        );
        AgendaEntry savedAgendaEntry = agendaRepository.save(agendaEntry);
        return ResponseEntity.status(CREATED).body(InlineAgendaEntry.from(savedAgendaEntry));
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> deleteAgendaEntry(@PathVariable String id) {
        agendaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/entries/{id}")
    public ResponseEntity<InlineAgendaEntry> updateAgendaEntry(@PathVariable String id, @RequestBody UpdateAgendaEntryRequest request) {
        AgendaEntry existing = agendaRepository.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // Update label if provided
        if (request.label() != null) {
            request.label().ifPresent(existing::setLabel);
        }

        // Update presentation if provided
        if (request.presentationId() != null) {
            request.presentationId().ifPresent(pId -> {
                Presentation presentation = presentationRepository.findById(pId);
                existing.setPresentation(presentation);
            });
        }

        // Update room if provided
        if (request.roomId() != null) {
            request.roomId().ifPresent(rId -> {
                Room room = roomRepository.findById(rId);
                existing.setRoom(room);
            });
        }

        AgendaEntry saved = agendaRepository.save(existing);
        return ResponseEntity.ok(InlineAgendaEntry.from(saved));
    }

    @GetMapping("/days")
    public List<InlineDay> getAllDays() {
        return dayRepository.findAll().stream().map(InlineDay::from).toList();
    }

    @GetMapping("/{dayId}/time-slots")
    public List<InlineTimeSlot> getAllTimeSlots(@PathVariable String dayId) {
        return timeSlotsRepository.findByIdDayId(dayId).stream()
                .map(InlineTimeSlot::from)
                .toList();
    }

    @GetMapping("/{dayId}/rooms")
    public List<InlineRoom> getAllRooms(@PathVariable String dayId) {
        return roomRepository.findByDayId(dayId).stream().map(InlineRoom::from).toList();
    }


}
