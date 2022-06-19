package pl.confitura.jelatyna.allegro.adapter.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderEvents {
    List<OrderEvent> events = new ArrayList<>();

    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public OrderEvent getLastEvent() {
        return events.get(events.size() - 1);
    }
}
