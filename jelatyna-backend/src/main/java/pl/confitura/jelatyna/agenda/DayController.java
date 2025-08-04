package pl.confitura.jelatyna.agenda;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/days")
public class DayController {

    private final DayRepository dayRepository;

    public DayController(DayRepository dayRepository) {
        this.dayRepository = dayRepository;
    }

    @GetMapping
    public Iterable<Day> getAllDays() {
        return dayRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Day> getDayById(@PathVariable String id) {
        Day day = dayRepository.findById(id);
        if (day == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(day);
    }

    @PostMapping
    public ResponseEntity<Day> saveDay(@RequestBody Day day) {
        Day savedDay = dayRepository.save(day);
        return ResponseEntity.status(CREATED).body(savedDay);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDay(@PathVariable String id) {
        dayRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}