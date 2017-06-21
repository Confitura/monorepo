package pl.confitura.jelatyna.agenda;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RepositoryRestResource(path = "rooms", excerptProjection = InlineRoom.class)
public interface RoomRepository extends Repository<Room, String> {

    @PreAuthorize("@security.isAdmin()")
    Room save(Room room);

    Room findOne(String id);

    @Query("select room from Room room order by displayOrder")
    Iterable<Room> findAll();

    @PreAuthorize("@security.isAdmin()")
    void delete(String id);

}
