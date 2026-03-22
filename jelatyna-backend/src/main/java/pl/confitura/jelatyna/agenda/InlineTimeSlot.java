package pl.confitura.jelatyna.agenda;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalTime;

interface InlineTimeSlot {
    String getId();

    @Value("#{target.getLabel()}")
    String getLabel();
    boolean getForAllRooms();
    int getDisplayOrder();

    LocalTime getStart();
    LocalTime getEnd();
}
