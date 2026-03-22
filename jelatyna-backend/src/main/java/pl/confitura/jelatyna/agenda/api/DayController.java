package pl.confitura.jelatyna.agenda.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.agenda.Day;
import pl.confitura.jelatyna.agenda.DayRepository;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/days")
public class DayController {

    private final DayRepository dayRepository;

    public DayController(DayRepository dayRepository) {
        this.dayRepository = dayRepository;
    }

    @GetMapping
    public List<InlineDay> getAllDays() {
        return dayRepository.findAll().stream().map(InlineDay::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InlineDay> getDayById(@PathVariable String id) {
        Day day = dayRepository.findById(id);
        if (day == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(InlineDay.from(day));
    }

    @PostMapping
    public ResponseEntity<InlineDay> saveDay(@RequestBody Day day) {
        Day savedDay = dayRepository.save(day);
        return ResponseEntity.status(CREATED).body(InlineDay.from(savedDay));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDay(@PathVariable String id) {
        dayRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}