package pl.confitura.jelatyna.agenda;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

@Service
@RequiredArgsConstructor
public class AgendaService {
    private final DayRepository dayRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;
    private final PresentationRepository presentationRepository;

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
}
