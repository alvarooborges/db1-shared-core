package net.hyze.core.shared.world.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SerializedLocation implements Cloneable {

    private String appId;

    @NonNull
    private String worldName = "world";

    private double x, y, z;

    private float yaw, pitch;

    public SerializedLocation(String appId, String worldName, double x, double y, double z) {
        this(appId, worldName, x, y, z, 0, 0);
    }

    public SerializedLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this(null, worldName, x, y, z, yaw, pitch);
    }

    public SerializedLocation(String worldName, double x, double y, double z) {
        this(null, worldName, x, y, z, 0, 0);
    }

    public <U extends LocationParser<T>, T> T parser(U parser) {
        return parser.apply(this);
    }

    public App getApp() {
        return CoreProvider.Cache.Local.APPS.provide().get(appId);
    }

    @Override
    public String toString() {
        try {
            return CoreConstants.JACKSON.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static SerializedLocation of(String string) {
        if (string != null) {
            try {
                return CoreConstants.JACKSON.readValue(string, SerializedLocation.class);
            } catch (IOException ex) {
                Logger.getGlobal().log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    @Override
    public SerializedLocation clone() {
        return new SerializedLocation(appId, worldName, x, y, z, yaw, pitch);
    }
}
