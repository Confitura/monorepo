package pl.confitura.jelatyna.agenda;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaService {
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;
    private final PresentationRepository presentationRepository;
    private final AgendaRepository agendaRepository;

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

        Map<String, List<AgendaEntry>> byPresentation = partitioned.get(true).stream().collect(Collectors.groupingBy(AgendaEntry::getPresentationId));
        var merged = byPresentation.entrySet().stream()
                .flatMap(it -> {
                    TimeSlotMerger reducer = TimeSlotMerger.empty();
                    it.getValue().stream()
                            .sorted(Comparator.comparing(AgendaEntry::getTimeSlotOrder))
                            .forEach(reducer::add);
                    it.getValue().getFirst();
                    return reducer.slots.stream();

                })
                .toList();
        var result = new ArrayList<>(merged);
        result.addAll(partitioned.get(false));
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
