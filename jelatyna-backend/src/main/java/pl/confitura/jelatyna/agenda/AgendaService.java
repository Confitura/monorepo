package pl.confitura.jelatyna.agenda;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaService {
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;
    private final PresentationRepository presentationRepository;
    private final AgendaRepository agendaRepository;
    private final DayRepository dayRepository;

    public AgendaEntry createAgendaEntry(String dayId, int timeSlotPosition, String roomId, String label, String presentationId) {
        TimeSlot timeSlot = timeSlotsRepository.findById(new TimeSlot.TimeSlotId(dayId, timeSlotPosition));
        Room room = roomId != null ? roomRepository.findById(roomId) : null;
        Presentation presentation = presentationId != null ? presentationRepository.findById(presentationId) : null;

        return new AgendaEntry()
                .setTimeSlot(timeSlot)
                .setRoom(room)
                .setLabel(label)
                .setPresentation(presentation);
    }

    /**
     * Appends a time slot to a day. The display order is only an identifier - slots are presented
     * in chronological order, so a slot may be added at any point of the day.
     */
    @Transactional
    public TimeSlot createTimeSlot(String dayId, LocalTime start, LocalTime end, boolean forAllRooms) {
        int displayOrder = timeSlotsRepository.findByIdDayId(dayId).stream()
                .mapToInt(TimeSlot::getDisplayOrder)
                .max()
                .orElse(-1) + 1;
        return timeSlotsRepository.save(new TimeSlot()
                .setId(new TimeSlot.TimeSlotId(dayId, displayOrder))
                .setStart(start)
                .setEnd(end)
                .setForAllRooms(forAllRooms));
    }

    /**
     * Removes a time slot together with all agenda entries scheduled in it.
     */
    @Transactional
    public void deleteTimeSlot(String dayId, int displayOrder) {
        TimeSlot.TimeSlotId id = new TimeSlot.TimeSlotId(dayId, displayOrder);
        agendaRepository.findByTimeSlotId(id).forEach(entry -> agendaRepository.deleteById(entry.getId()));
        timeSlotsRepository.deleteById(id);
    }

    @Transactional
    public Room createRoom(String dayId, String label, Integer displayOrder) {
        int order = displayOrder != null ? displayOrder : nextRoomOrder(dayId);
        return roomRepository.save(new Room()
                .setId(UUID.randomUUID().toString())
                .setLabel(label)
                .setDisplayOrder(order)
                .setDay(dayRepository.findById(dayId)));
    }

    /**
     * Removes a room together with all agenda entries scheduled in it.
     */
    @Transactional
    public void deleteRoom(String roomId) {
        agendaRepository.findByRoomId(roomId).forEach(entry -> agendaRepository.deleteById(entry.getId()));
        roomRepository.deleteById(roomId);
    }

    /**
     * Removes a day together with its rooms, time slots and agenda entries.
     */
    @Transactional
    public void deleteDay(String dayId) {
        roomRepository.findByDayId(dayId).forEach(room -> deleteRoom(room.getId()));
        timeSlotsRepository.findByIdDayId(dayId)
                .forEach(slot -> deleteTimeSlot(dayId, slot.getDisplayOrder()));
        dayRepository.deleteById(dayId);
    }

    private int nextRoomOrder(String dayId) {
        return roomRepository.findByDayId(dayId).stream()
                .mapToInt(Room::getDisplayOrder)
                .max()
                .orElse(0) + 1;
    }

    public List<AgendaEntry> findAllAndMerge() {
        List<AgendaEntry> entries = agendaRepository.findAll();
        return mergeTimeSlots(entries);
    }

    public List<AgendaEntry> findByTimeSlotIdDayIdAndMerge(String dayId) {
        List<AgendaEntry> entries = agendaRepository.findByTimeSlotIdDayId(dayId);
        return mergeTimeSlots(entries);
    }

    @NotNull
    private static ArrayList<AgendaEntry> mergeTimeSlots(List<AgendaEntry> entries) {
        Map<Boolean, List<AgendaEntry>> partitioned = entries.stream().collect(Collectors.partitioningBy(AgendaEntry::hasPresentation));
        List<AgendaEntry> withoutPresentation = partitioned.get(false);
        List<AgendaEntry> withPresentation = partitioned.get(true);

        Map<String, List<AgendaEntry>> byPresentation = withPresentation.stream().collect(Collectors.groupingBy(AgendaEntry::getPresentationId));
        var merged = byPresentation.entrySet().stream()
                .flatMap(it -> {
                    TimeSlotMerger reducer = TimeSlotMerger.empty();
                    it.getValue().stream()
                            .sorted(Comparator.comparing((AgendaEntry entry) -> entry.getTimeSlot().getStart()))
                            .forEach(reducer::add);
                    it.getValue().getFirst();
                    return reducer.slots.stream();

                })
                .toList();
        var result = new ArrayList<>(merged);
        result.addAll(withoutPresentation);
        return result;
    }

    static class TimeSlotMerger {

        private List<AgendaEntry> slots = new ArrayList<>();
        private AgendaEntry last = null;

        public static TimeSlotMerger empty() {
            return new TimeSlotMerger();
        }

        public void add(AgendaEntry entry) {
            if (last != null && entry.getPresentation() != null) {
                var ts1 = last.getTimeSlot();
                var ts2 = entry.getTimeSlot();
                if (areInSequence(ts1, ts2)) {

                    TimeSlot mergedSlot = ts1.mergeWith(ts2);
                    AgendaEntry mergedEntry = new AgendaEntry()
                            .setRoom(last.getRoom())
                            .setTimeSlot(mergedSlot)
                            .setId(last.getId())
                            .setLabel(last.getLabel())
                            .setPresentation(last.getPresentation());

                    slots.remove(last);
                    last = mergedEntry;
                    slots.add(mergedEntry);
                    return;
                }
            }
            last = entry;
            slots.add(entry);

        }

        private static boolean areInSequence(TimeSlot ts1, TimeSlot ts2) {
            return ts2.getStart().equals(ts1.getEnd())
                   && ts1.getId().dayId().equals(ts2.getId().dayId());
        }
    }

}
