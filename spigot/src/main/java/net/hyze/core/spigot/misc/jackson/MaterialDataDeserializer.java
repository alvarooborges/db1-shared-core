package net.hyze.core.spigot.misc.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.hyze.core.shared.misc.utils.NumberUtils;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class MaterialDataDeserializer extends StdDeserializer<MaterialData> {

    public MaterialDataDeserializer() {
        super(MaterialData.class);
    }

    @Override
    public MaterialData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode root = p.readValueAsTree();

        String text = root.asText().toUpperCase();
        String[] split = text.split(":");

        Material type = Material.matchMaterial(split[0]);

        if(type == null) {
            throw new IllegalArgumentException("Invalid material type: " + root.asText().toUpperCase());
        }

        byte data = 0;
        if(split.length > 1) {
            try {
                data = Byte.parseByte(split[1]);
            } catch(NumberFormatException ex) {
            }
        }

        return new MaterialData(type, data);
    }
}
