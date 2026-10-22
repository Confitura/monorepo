package pl.confitura.jelatyna.agenda;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomRepository roomRepository;
    private final DayRepository dayRepository;

    public RoomController(RoomRepository roomRepository, DayRepository dayRepository) {
        this.roomRepository = roomRepository;
        this.dayRepository = dayRepository;
    }

    @GetMapping
    public Iterable<Room> getAllRooms() {
        return roomRepository.findAllInCurrentEdition();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable String id) {
        Room room = roomRepository.findInCurrentEditionById(id);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    @PostMapping
    public ResponseEntity<Room> saveRoom(@RequestBody Room room) {
        Day day = dayRepository.findInCurrentEditionById(room.getDay().getId());
        if (day == null) {
            return ResponseEntity.notFound().build();
        }
        // Room ids are global: saving another Edition's room id would take that room over (ADR-0004).
        Room existing = roomRepository.findById(room.getId());
        if (existing != null && !existing.getDay().getEdition().isCurrent()) {
            return ResponseEntity.status(CONFLICT).build();
        }
        Room savedRoom = roomRepository.save(room.setDay(day));
        return ResponseEntity.status(CREATED).body(savedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        if (roomRepository.findInCurrentEditionById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        roomRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
