package pl.confitura.jelatyna.allegro.adapter.dto;

import lombok.Data;

@Data
public class OrderEvent {
    private String id;
    private String type;
    private Order order;
    private String occurredAt;

    public long getOrderedQuantityChange() {
        return getOrderedCountChange() * order.getTotalQuantity();
    }

    public long getOrderedCountChange() {
        if (type.equals("BOUGHT")) {
            return 1;
        } else if (type.contains("CANCELLED")) {
            return -1;
        } else {
            return 0;
        }
    }
}
