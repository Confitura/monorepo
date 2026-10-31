package pl.confitura.jelatyna.agenda.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.agenda.AgendaService;
import pl.confitura.jelatyna.agenda.Day;
import pl.confitura.jelatyna.agenda.DayRepository;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/days")
public class DayController {

    private final DayRepository dayRepository;
    private final AgendaService agendaService;
    private final EditionRepository editionRepository;

    public DayController(DayRepository dayRepository, AgendaService agendaService, EditionRepository editionRepository) {
        this.dayRepository = dayRepository;
        this.agendaService = agendaService;
        this.editionRepository = editionRepository;
    }

    @GetMapping
    public List<InlineDay> getAllDays() {
        return dayRepository.findAllInCurrentEdition().stream().map(InlineDay::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InlineDay> getDayById(@PathVariable String id) {
        Day day = dayRepository.findInCurrentEditionById(id);
        if (day == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(InlineDay.from(day));
    }

    @PostMapping
    public ResponseEntity<InlineDay> saveDay(@RequestBody Day day) {
        Day savedDay = dayRepository.save(day.setEdition(editionRepository.findCurrent()));
        return ResponseEntity.status(CREATED).body(InlineDay.from(savedDay));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InlineDay> updateDay(@PathVariable String id, @RequestBody UpdateDayRequest request) {
        Day day = dayRepository.findInCurrentEditionById(id);
        if (day == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            day.setDate(LocalDate.parse(request.date()));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        day.setLabel(request.label());
        day.setDisplayOrder(request.displayOrder());
        return ResponseEntity.ok(InlineDay.from(dayRepository.save(day)));
    }

    /**
     * Deletes a day with its rooms, time slots and agenda entries.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDay(@PathVariable String id) {
        Day day = dayRepository.findInCurrentEditionById(id);
        if (day == null) {
            return ResponseEntity.notFound().build();
        }
        agendaService.deleteDay(day);
        return ResponseEntity.noContent().build();
    }
}