package pl.confitura.jelatyna.conference.agenda.domain;

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

    List<Room> findByDay(Day day);

    // A room follows its Day's Edition: the admin agenda edits only the Current Edition's (ADR-0004).
    @Query("select room from Room room where room.day.edition.current = true order by room.displayOrder")
    List<Room> findAllInCurrentEdition();

    @Query("select room from Room room where room.id = :id and room.day.edition.current = true")
    Room findInCurrentEditionById(String id);

    @Query("select room from Room room where room.day.id = :dayId and room.day.edition.current = true")
    List<Room> findInCurrentEditionByDayId(String dayId);

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);

}
