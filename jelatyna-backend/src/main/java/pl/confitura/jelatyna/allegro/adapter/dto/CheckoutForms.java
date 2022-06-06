package pl.confitura.jelatyna.allegro.adapter.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckoutForms {
    private List<CheckoutForm> checkoutForms;
    private Long count;
    private Long totalCount;

}
