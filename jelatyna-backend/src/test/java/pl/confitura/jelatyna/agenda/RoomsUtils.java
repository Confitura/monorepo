package pl.confitura.jelatyna.agenda;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.ADMIN_TOKEN;

@Service
public class RoomsUtils {

    private final RoomRepository roomRepository;

    public RoomsUtils(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }


    public List<Room> createRooms(List<String> roomNames) {
        return roomNames.stream()
                .map(this::createRoom)
                .collect(toList());
    }

    private Room createRoom(String label) {
        SecurityContextHolder.getContext().setAuthentication(ADMIN_TOKEN);
        RequestContextHolder.getRequestAttributes();
        try {
            Room room = new Room().setLabel(label);
            return roomRepository.save(room);
        } catch (Exception e) {
            return null;
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
