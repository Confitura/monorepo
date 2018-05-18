package pl.confitura.jelatyna.agenda;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalTime;

@Projection(name = "inlineTimeSlot", types = {TimeSlot.class})
interface InlineTimeSlot {
    String getId();

    @Value("#{target.getLabel()}")
    String getLabel();
    boolean getForAllRooms();
    int getDisplayOrder();

    LocalTime getStart();
    LocalTime getEnd();
}
