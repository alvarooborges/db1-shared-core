package net.hyze.core.spigot.misc.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class PotionEffectTypeDeserializer extends StdDeserializer<PotionEffectType> {

    public PotionEffectTypeDeserializer() {
        super(PotionEffectType.class);
    }

    @Override
    public PotionEffectType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode root = p.readValueAsTree();
        PotionEffectType type = PotionEffectType.getByName(root.asText().toUpperCase());

        if(type == null) {
            throw new IllegalArgumentException("Invalid potion type: " + root.asText().toUpperCase());
        }

        return type;
    }
}
