package pl.confitura.jelatyna.allegro.adapter.dto;

import lombok.Data;

@Data
public class OrderEvent {
    private String id;
    private String type;
    private String occurredAt;

    public String[] csvLine() {
        return new String[]{id, type, occurredAt};
    }
}
