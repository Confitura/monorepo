package pl.confitura.jelatyna.allegro.adapter.dto;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    List<LineItem> lineItems;

    public long getTotalQuantity() {
        if (lineItems == null) {
            return 0;
        }
        return lineItems.stream().mapToLong(LineItem::getQuantity).sum();
    }
}
