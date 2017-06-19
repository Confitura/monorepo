package pl.confitura.jelatyna.agenda;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "inlineRoom", types = {Room.class})
interface InlineRoom {
    String getId();
    String getLabel();

}
