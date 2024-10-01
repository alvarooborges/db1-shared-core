package net.hyze.core.shared.misc.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.hyze.core.shared.world.location.LocationParser;
import net.hyze.core.shared.world.location.SerializedLocation;

import java.io.IOException;

public class LocationDeserializer<T> extends StdDeserializer<T> {

    private final LocationParser<T> parser;

    public LocationDeserializer(Class<T> t, LocationParser<T> parser) {
        super(t);

        this.parser = parser;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SerializedLocation loc = p.readValueAs(SerializedLocation.class);
        return loc.parser(this.parser);
    }
}
