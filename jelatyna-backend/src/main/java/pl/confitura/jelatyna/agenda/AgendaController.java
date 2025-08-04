package pl.confitura.jelatyna.agenda;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/agenda")
public class AgendaController {

    private final AgendaRepository agendaRepository;
    private final DayRepository dayRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;

    public AgendaController(AgendaRepository agendaRepository, DayRepository dayRepository,
                           TimeSlotsRepository timeSlotsRepository, RoomRepository roomRepository) {
        this.agendaRepository = agendaRepository;
        this.dayRepository = dayRepository;
        this.timeSlotsRepository = timeSlotsRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public Iterable<AgendaEntry> getAllAgendaEntries() {
        return agendaRepository.findAll();
    }

    @GetMapping("/day/{dayId}")
    public Iterable<AgendaEntry> getAgendaEntriesByDay(@PathVariable String dayId) {
        Day day = dayRepository.findById(dayId);
        if (day == null) {
            return null;
        }
        return agendaRepository.findByDay(day);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaEntry> getAgendaEntryById(@PathVariable String id) {
        AgendaEntry agendaEntry = agendaRepository.findById(id);
        if (agendaEntry == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(agendaEntry);
    }

    @PostMapping
    public ResponseEntity<AgendaEntry> saveAgendaEntry(@RequestBody AgendaEntry agendaEntry) {
        AgendaEntry savedAgendaEntry = agendaRepository.save(agendaEntry);
        return ResponseEntity.status(CREATED).body(savedAgendaEntry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgendaEntry(@PathVariable String id) {
        agendaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/days")
    public Iterable<Day> getAllDays() {
        return dayRepository.findAll();
    }

    @GetMapping("/time-slots")
    public Iterable<TimeSlot> getAllTimeSlots() {
        return timeSlotsRepository.findAll();
    }

    @GetMapping("/rooms")
    public Iterable<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}
