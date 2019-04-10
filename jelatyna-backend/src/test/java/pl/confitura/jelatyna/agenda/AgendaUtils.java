package pl.confitura.jelatyna.agenda;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.presentation.Presentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class AgendaUtils {

    private final RoomsUtils roomsUtils;
    private final TimeSlotUtils timeSlotUtils;
    private final AgendaRepository agendaRepository;
    private final PresentationUtils presentationUtils;

    @Transactional
    public List<AgendaEntry> createAgenda(String[]... agenda) {
        List<String> roomNames = extractRooms(agenda);
        List<String> timeSlotNames = extractTimeSlots(agenda);
        List<List<String>> presentationNames = extractPresentations(agenda);

        List<Room> rooms = roomsUtils.createRooms(roomNames);
        List<TimeSlot> timeSlots = timeSlotUtils.createSlots(timeSlotNames, presentationNames);
        List<List<Presentation>> pressentations = presentationUtils.createPresentationsMatrix(presentationNames);

        createAgenda(rooms, timeSlots, pressentations);
        Iterable<AgendaEntry> iterableAgenda = agendaRepository.findAll();
        return StreamSupport
                .stream(iterableAgenda.spliterator(), false)
                .peek(it->it.getPresentation().getTitle())
                .peek(it->it.getPresentation().getTags().size())
                .peek(it->it.getPresentation().getSpeakers().size())
                .distinct()
                .collect(Collectors.toList());

    }

    private void createAgenda(List<Room> rooms, List<TimeSlot> timeSlots, List<List<Presentation>> pressentations) {
        for (int timeSlotIndex = 0; timeSlotIndex < timeSlots.size(); timeSlotIndex++) {
            TimeSlot timeSlot = timeSlots.get(timeSlotIndex);
            if (timeSlot.isForAllRooms()) {
                Presentation presentation = pressentations.get(timeSlotIndex).get(0);
                AgendaEntry agendaEntry = new AgendaEntry()
                        .setPresentation(presentation)
                        .setTimeSlot(timeSlot);
                agendaRepository.save(agendaEntry);
            } else {
                for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
                    Room room = rooms.get(roomIndex);
                    Presentation presentation = pressentations.get(timeSlotIndex).get(roomIndex);
                    AgendaEntry agendaEntry = new AgendaEntry()
                            .setPresentation(presentation)
                            .setRoom(room)
                            .setTimeSlot(timeSlot);
                    agendaRepository.save(agendaEntry);
                }
            }
        }
    }

    private List<List<String>> extractPresentations(String[][] agenda) {
        List<List<String>> presentations = new ArrayList<>();
        for (int i = 1; i < agenda.length; i++) {
            List<String> list = Arrays.asList(agenda[i]).subList(1, agenda[i].length);
            presentations.add(list);
        }
        return presentations;
    }

    private List<String> extractTimeSlots(String[][] agenda) {
        ArrayList<String> rooms = new ArrayList<>();
        for (int i = 1; i < agenda.length; i++) {
            rooms.add(agenda[i][0]);
        }
        return rooms;
    }

    private List<String> extractRooms(String[][] agenda) {
        String[] firstRow = agenda[0];
        return Arrays.asList(firstRow).subList(1, firstRow.length);

    }
}
