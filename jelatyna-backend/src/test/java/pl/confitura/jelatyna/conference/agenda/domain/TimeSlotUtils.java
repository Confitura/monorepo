package pl.confitura.jelatyna.conference.agenda.domain;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.ADMIN_TOKEN;

@Component
@AllArgsConstructor
public class TimeSlotUtils {

    private final TimeSlotsRepository timeSlotsRepository;
    private final EditionRepository editionRepository;


    public List<TimeSlot> createSlots(List<String> timeSlotNames, List<List<String>> presentationNames) {
        SecurityContextHolder.getContext().setAuthentication(ADMIN_TOKEN);
        List<TimeSlot> timeSlots = new ArrayList<>();

        for (int i = 0; i < timeSlotNames.size(); i++) {
            boolean allRoomsSlot = presentationNames.get(i).size() == 1;
            var slotString = timeSlotNames.get(i).split("-");
            var start = LocalTime.of(Integer.parseInt(slotString[0]), 0);
            var end = LocalTime.of(Integer.parseInt(slotString[1]), 0);

            TimeSlot timeSlot = new TimeSlot()
                    .setId(new TimeSlot.TimeSlotId(editionRepository.findCurrent().getId(), "day-1", i))
                    .setStart(start)
                    .setEnd(end)
                    .setForAllRooms(allRoomsSlot);
            timeSlots.add(timeSlotsRepository.save(timeSlot));
        }

        return timeSlots;
    }
}
