package pl.confitura.jelatyna.infrastructure;

import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

@Component
public class ResourceSerializer extends ValueSerializer<String> {

    @Override
    public void serialize(String resourcePath, JsonGenerator jsonGenerator, SerializationContext context)
            throws JacksonException {
        jsonGenerator.writeString("resources/" + resourcePath);

    }
}
