package pl.confitura.jelatyna.agenda;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "inlineTimeSlot", types = {TimeSlot.class})
interface InlineTimeSlot {
    String getId();
    String getLabel();
    boolean getForAllRooms();
    int getDisplayOrder();

}
