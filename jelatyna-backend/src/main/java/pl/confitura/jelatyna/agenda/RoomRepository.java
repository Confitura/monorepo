package pl.confitura.jelatyna.agenda;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

public interface RoomRepository extends Repository<Room, String> {

    @PreAuthorize("@security.isAdmin()")
    Room save(Room room);

    Room findById(String id);

    @Query("select room from Room room order by displayOrder")
    Iterable<Room> findAll();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);

}
