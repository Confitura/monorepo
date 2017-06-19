package pl.confitura.jelatyna.agenda;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RestResource(path = "rooms")
public interface RoomRepository extends Repository<Room, String> {

    @PreAuthorize("@security.isAdmin()")
    Room save(Room room);

    Room findOne(String id);

    Iterable<Room> findAll();

    @PreAuthorize("@security.isAdmin()")
    void delete(String id);

}
