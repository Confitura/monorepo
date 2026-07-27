package pl.confitura.jelatyna.agenda.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.agenda.*;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;
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
        private final pl.confitura.jelatyna.agenda.IcalExportService icalExportService;


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

        existing.setLabel(request.label().orElse(null));
        existing.setPresentation(request.presentationId().map(presentationRepository::findById).orElse(null));
        existing.setRoom(request.roomId().map(roomRepository::findById).orElse(null));

        AgendaEntry saved = agendaRepository.save(existing);
        return ResponseEntity.ok(InlineAgendaEntry.from(saved));
    }

    @GetMapping("/days")
    public List<InlineDay> getAllDays() {
        return dayRepository.findAll().stream()
                .sorted(comparing(Day::getDisplayOrder))
                .map(InlineDay::from)
                .toList();
    }

    @PutMapping("/entries/{id}/slot")
    public ResponseEntity<InlineAgendaEntry> moveAgendaEntry(@PathVariable String id, @RequestBody UpdateAgendaEntrySlotRequest request) {
        AgendaEntry existing = agendaRepository.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        TimeSlot timeSlot = timeSlotsRepository.findById(new TimeSlot.TimeSlotId(request.dayId(), request.timeSlotIndex()));
        if (timeSlot == null) {
            return ResponseEntity.notFound().build();
        }
        existing.setTimeSlot(timeSlot);
        existing.setRoom(request.roomId().map(roomRepository::findById).orElse(null));

        AgendaEntry saved = agendaRepository.save(existing);
        return ResponseEntity.ok(InlineAgendaEntry.from(saved));
    }

    @GetMapping("/{dayId}/time-slots")
    public List<InlineTimeSlot> getAllTimeSlots(@PathVariable String dayId) {
        return timeSlotsRepository.findByIdDayId(dayId).stream()
                .sorted(TimeSlot.CHRONOLOGICALLY)
                .map(InlineTimeSlot::from)
                .toList();
    }

    @PostMapping("/{dayId}/time-slots")
    public ResponseEntity<InlineTimeSlot> createTimeSlot(@PathVariable String dayId,
                                                         @RequestBody CreateTimeSlotRequest request) {
        if (dayRepository.findById(dayId) == null) {
            return ResponseEntity.notFound().build();
        }
        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(request.start());
            end = LocalTime.parse(request.end());
        } catch (DateTimeParseException | NullPointerException e) {
            return ResponseEntity.badRequest().build();
        }
        TimeSlot created = agendaService.createTimeSlot(dayId, start, end, request.forAllRooms().orElse(false));
        return ResponseEntity.status(CREATED).body(InlineTimeSlot.from(created));
    }

    /**
     * Deletes a time slot with all agenda entries scheduled in it.
     */
    @DeleteMapping("/{dayId}/time-slots/{displayOrder}")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable String dayId, @PathVariable int displayOrder) {
        if (timeSlotsRepository.findById(new TimeSlot.TimeSlotId(dayId, displayOrder)) == null) {
            return ResponseEntity.notFound().build();
        }
        agendaService.deleteTimeSlot(dayId, displayOrder);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{dayId}/rooms")
    public List<InlineRoom> getAllRooms(@PathVariable String dayId) {
        return roomRepository.findByDayId(dayId).stream()
                .sorted(Comparator.comparing(Room::getDisplayOrder))
                .map(InlineRoom::from)
                .toList();
    }

    @PostMapping("/{dayId}/rooms")
    public ResponseEntity<InlineRoom> createRoom(@PathVariable String dayId, @RequestBody CreateRoomRequest request) {
        if (dayRepository.findById(dayId) == null) {
            return ResponseEntity.notFound().build();
        }
        Room created = agendaService.createRoom(dayId, request.label(), request.displayOrder().orElse(null));
        return ResponseEntity.status(CREATED).body(InlineRoom.from(created));
    }

    // New endpoint: update Room
    @PutMapping("/rooms/{id}")
    public ResponseEntity<InlineRoom> updateRoom(@PathVariable String id, @RequestBody UpdateRoomRequest request) {
        Room room = roomRepository.findById(id);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        if (request.label() != null) {
            request.label().ifPresent(room::setLabel);
        }
        if (request.displayOrder() != null) {
            request.displayOrder().ifPresent(room::setDisplayOrder);
        }
        Room saved = roomRepository.save(room);
        return ResponseEntity.ok(InlineRoom.from(saved));
    }

    /**
     * Deletes a room with all agenda entries scheduled in it.
     */
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> removeRoom(@PathVariable String id) {
        if (roomRepository.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        agendaService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    // New endpoint: update TimeSlot (by composite id)
    @PutMapping("/{dayId}/time-slots/{displayOrder}")
    public ResponseEntity<InlineTimeSlot> updateTimeSlot(@PathVariable String dayId,
                                                         @PathVariable int displayOrder,
                                                         @RequestBody UpdateTimeSlotRequest request) {
        TimeSlot.TimeSlotId timeSlotId = new TimeSlot.TimeSlotId(dayId, displayOrder);
        TimeSlot slot = timeSlotsRepository.findById(timeSlotId);
        if (slot == null) {
            return ResponseEntity.notFound().build();
        }
        // parse and update times if provided
        try {
            if (request.start() != null) {
                request.start().ifPresent(s -> slot.setStart(LocalTime.parse(s)));
            }
            if (request.end() != null) {
                request.end().ifPresent(e -> slot.setEnd(LocalTime.parse(e)));
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        if (request.forAllRooms() != null) {
            request.forAllRooms().ifPresent(slot::setForAllRooms);
        }
        TimeSlot saved = timeSlotsRepository.save(slot);
        return ResponseEntity.ok(InlineTimeSlot.from(saved));
    }

    @GetMapping(value = "/ical", produces = "text/calendar")
    public ResponseEntity<byte[]> exportIcal() {
        byte[] body = icalExportService.generateIcs();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=agenda.ics")
                .body(body);
    }

    @GetMapping(value = "/ical/subscribe", produces = "text/calendar")
    public ResponseEntity<byte[]> subscribeIcal() {
        byte[] body = icalExportService.generateIcs();
        return ResponseEntity.ok()
                .header("Cache-Control", "public, max-age=3000")
                .body(body);
    }

}
