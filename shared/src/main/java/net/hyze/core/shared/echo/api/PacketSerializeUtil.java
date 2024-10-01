package net.hyze.core.shared.echo.api;

import com.google.common.base.Enums;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PacketSerializeUtil {

    public static void writeString(EchoBufferOutput buffer, String str) {
        buffer.writeString(str);
    }

    public static String readString(EchoBufferInput buffer) {
        return buffer.readString();
    }

    public static void writeUUID(EchoBufferOutput buffer, UUID uuid) {
        buffer.writeString(Optional.ofNullable(uuid).map(UUID::toString).orElse(null));
    }

    public static UUID readUUID(EchoBufferInput buffer) {
        String str = buffer.readString();

        if (str != null) {
            return UUID.fromString(str);
        }

        return null;
    }

    public static void writeDate(EchoBufferOutput buffer, Date date) {
        buffer.writeLong(Optional.ofNullable(date)
                .map(Date::getTime)
                .orElse(-1L)
        );
    }

    public static Date readDate(EchoBufferInput buffer) {
        long time = buffer.readLong();

        if (time > 0) {
            return new Date(time);
        }

        return null;
    }

    public static void writeAddress(EchoBufferOutput buffer, InetSocketAddress address) {
        InetAddress addr = address.getAddress();
        String str = addr == null ? address.getHostName() : addr.toString().trim();
        int ix = str.indexOf('/');
        if (ix >= 0) {
            if (ix == 0) { // missing host name; use address
                str = addr instanceof Inet6Address
                        ? "[" + str.substring(1) + "]" // bracket IPv6 addresses with
                        : str.substring(1);

            } else { // otherwise use name
                str = str.substring(0, ix);
            }
        }

        buffer.writeString(str + ":" + address.getPort());
    }

    public static InetSocketAddress readAddress(EchoBufferInput buffer) {
        String value = buffer.readString();

        if (value == null) {
            return null;
        }

        if (value.startsWith("[")) {
            // bracketed IPv6 (with port number)

            int i = value.lastIndexOf(']');
            if (i == -1) {
                return null;
            }

            int j = value.indexOf(':', i);
            int port = j > -1 ? Integer.parseInt(value.substring(j + 1)) : 0;
            return new InetSocketAddress(value.substring(0, i + 1), port);
        } else {
            int i = value.indexOf(':');
            if (i != -1 && value.indexOf(':', i + 1) == -1) {
                // host:port
                int port = Integer.parseInt(value.substring(i + 1));
                return new InetSocketAddress(value.substring(0, i), port);
            } else {
                // host or unbracketed IPv6, without port number
                return new InetSocketAddress(value, 0);
            }
        }
    }

    public static void writeServer(EchoBufferOutput buffer, Server server) {
        buffer.writeString(Optional.ofNullable(server).map(Server::getId).orElse(null));
    }

    public static Server readServer(EchoBufferInput buffer) {
        String id = buffer.readString();

        if (id != null) {
            return Server.getById(id).orNull();
        }

        return null;
    }

    public static void writeApp(EchoBufferOutput buffer, App app) {
        if (app != null) {
            buffer.writeBoolean(true);
            buffer.writeString(app.getId());
            buffer.writeString(app.getDisplayName());
            writeEnum(buffer, app.getType());
            writeAddress(buffer, app.getAddress());
            writeEnum(buffer, app.getServer());
        } else {
            buffer.writeBoolean(false);
        }
    }

    public static App readApp(EchoBufferInput buffer) {
        boolean valid = buffer.readBoolean();

        if (!valid) {
            return null;
        }

        return new App(
                buffer.readString(),
                buffer.readString(),
                readEnum(buffer, AppType.class),
                readAddress(buffer),
                readEnum(buffer, Server.class)
        );
    }

    public static void writeUser(EchoBufferOutput buffer, User user) {
        buffer.writeInt(Optional.ofNullable(user).map(User::getId).orElse(-1));
    }

    public static User readUser(EchoBufferInput buffer) {
        int id = buffer.readInt();

        if (id > 0) {
            return CoreProvider.Cache.Local.USERS.provide().get(id);
        }

        return null;
    }

    public static void writeSerializedLocation(EchoBufferOutput buffer, SerializedLocation location) {
        buffer.writeString(Optional.ofNullable(location).map(SerializedLocation::toString).orElse(null));
    }

    public static SerializedLocation readSerializedLocation(EchoBufferInput buffer) {
        String str = buffer.readString();

        if (str != null) {
            return SerializedLocation.of(str);
        }

        return null;
    }

    public static <T extends Enum<T>> void writeEnum(EchoBufferOutput buffer, T value) {
        buffer.writeString(Optional.ofNullable(value).map(Enum::name).orElse(null));
    }

    public static <T extends Enum<T>> T readEnum(EchoBufferInput buffer, Class<T> clazz) {
        return readEnum(buffer, clazz, null);
    }

    public static <T extends Enum<T>> T readEnum(EchoBufferInput buffer, Class<T> clazz, T deft) {
        String str = buffer.readString();

        if (str != null) {
            com.google.common.base.Optional<T> optional = Enums.getIfPresent(clazz, str);

            if (deft != null) {
                return optional.or(deft);
            }

            return optional.orNull();
        }

        return deft;
    }

    public static void writeJsonObject(EchoBufferOutput buffer, JsonObject json) {
        if (json != null) {
            writeString(buffer, json.toString());
        } else {
            writeString(buffer, null);
        }
    }

    public static JsonObject readJsonObject(EchoBufferInput buffer) {
        String str = readString(buffer);

        if (str == null) {
            return null;
        }

        return new JsonParser().parse(str).getAsJsonObject();
    }
}
