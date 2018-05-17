package pl.confitura.jelatyna.agenda;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.ADMIN_TOKEN;

@Component
@AllArgsConstructor
public class TimeSlotUtils {

    private final TimeSlotsRepository timeSlotsRepository;


    public List<TimeSlot> createSlots(List<String> timeSlotNames, List<List<String>> presentationNames) {
        SecurityContextHolder.getContext().setAuthentication(ADMIN_TOKEN);
        List<TimeSlot> timeSlots = new ArrayList<>();

        for (int i = 0; i < timeSlotNames.size(); i++) {
            boolean allRoomsSlot = presentationNames.get(i).size() == 1;
            TimeSlot timeSlot = new TimeSlot()
                    .setDisplayOrder(i)
                    .setLabel(timeSlotNames.get(i))
                    .setForAllRooms(allRoomsSlot);
            timeSlots.add(timeSlotsRepository.save(timeSlot));
        }

        return timeSlots;
    }
}
