package pl.confitura.jelatyna.platform.web;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

class ResourceSerializer extends ValueSerializer<String> {

    @Override
    public void serialize(String resourcePath, JsonGenerator jsonGenerator, SerializationContext context)
            throws JacksonException {
        jsonGenerator.writeString("resources/" + resourcePath);

    }
}
