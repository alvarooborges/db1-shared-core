package net.hyze.core.shared.world.materialdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.hyze.core.shared.CoreConstants;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"materialRaw", "data"})
public class SerializedMaterialData implements Cloneable {

    protected String materialRaw;

    protected byte data;

    public SerializedMaterialData(String materialRaw) {
        this(materialRaw, (byte) 0);
    }

    public <U extends MaterialDataParser<T>, T> T parser(U parser) {
        return parser.apply(this);
    }

    @Override
    public String toString() {
        return String.format("%s:%d", this.materialRaw, this.data);
    }

    public static SerializedMaterialData of(String string) {
        if (string != null) {
            byte data = 0;

            String[] split = string.split(":");
            if (split.length > 0) {
                try {
                    data = Byte.parseByte(split[1]);
                } catch (NumberFormatException ignored) {

                }
            }

            return new SerializedMaterialData(split[0], data);
        }

        return null;
    }

    @Override
    public SerializedMaterialData clone() {
        return new SerializedMaterialData(this.materialRaw, this.data);
    }
}
