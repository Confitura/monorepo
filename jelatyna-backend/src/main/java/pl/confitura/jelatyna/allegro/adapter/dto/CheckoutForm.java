package pl.confitura.jelatyna.allegro.adapter.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CheckoutForm {
    private String id;
    private String messageToSeller;
    private Buyer buyer;
    private List<LineItem> lineItems = new ArrayList<>();

    public Long getQuantity() {
        return lineItems.stream().mapToLong(it -> it.getQuantity()).sum();
    }
}
