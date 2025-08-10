package pl.confitura.jelatyna.agenda;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface RoomRepository extends Repository<Room, String> {

    @PreAuthorize("@security.isAdmin()")
    Room save(Room room);

    Room findById(String id);

    @Query("select room from Room room order by displayOrder")
    List<Room> findAll();

    List<Room> findByDayId(String dayId);

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);

}
