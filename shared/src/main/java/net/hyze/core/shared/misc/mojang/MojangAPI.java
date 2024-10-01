package net.hyze.core.shared.misc.mojang;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.misc.mojang.exceptions.SkinNotFoundException;
import net.hyze.core.shared.misc.mojang.exceptions.TooManyRequestsException;
import net.hyze.core.shared.misc.mojang.exceptions.UUIDNotFoundException;
import net.hyze.core.shared.misc.utils.Patterns;
import net.hyze.core.shared.skins.Skin;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import org.apache.commons.io.IOUtils;

public class MojangAPI {

    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private static final String SKIN_BLOB_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    /*
     * 
     */
    public static UUID getUUID(String nick) throws IOException, TooManyRequestsException, UUIDNotFoundException {

        HttpURLConnection connection = (HttpURLConnection) setupConnection(new URL(PROFILE_URL));
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            writer.write(CoreConstants.GSON.toJson(Arrays.asList(nick)).getBytes(StandardCharsets.UTF_8));
            writer.flush();
        }

        if (connection.getResponseCode() == 429) {
            throw new TooManyRequestsException(connection.getResponseCode());
        }

        InputStream is = connection.getInputStream();
        String result = IOUtils.toString(is, StandardCharsets.UTF_8);
        IOUtils.closeQuietly(is);

        JsonArray jsonProfiles = new JsonParser().parse(result).getAsJsonArray();

        if (jsonProfiles.size() > 0) {

            JsonObject jsonProfile = jsonProfiles.get(0).getAsJsonObject();
            String rawUuid = (String) jsonProfile.get("id").getAsString();

            return fromUntracedUuidString(rawUuid);

        }

        throw new UUIDNotFoundException();

    }

    public static Skin getSkin(UUID uuid) throws IOException, TooManyRequestsException, SkinNotFoundException {

        Preconditions.checkNotNull(uuid, "UUID cannot be null.");

        HttpURLConnection connection = (HttpURLConnection) setupConnection(new URL(SKIN_BLOB_URL + Patterns.HYPHEN.replace(uuid.toString(), "") + "?unsigned=false"));

        if (connection.getResponseCode() == 429) {
            throw new TooManyRequestsException(connection.getResponseCode());
        }

        InputStream is = connection.getInputStream();
        String result = IOUtils.toString(is, StandardCharsets.UTF_8);
        IOUtils.closeQuietly(is);

        JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
        JsonArray properties = obj.get("properties").getAsJsonArray();

        for (JsonElement element : properties) {
            JsonObject property = element.getAsJsonObject();

            String name = property.get("name").getAsString();
            if (name.equals("textures")) {
                String value = property.get("value").getAsString();
                String signature = property.get("signature").getAsString();

                return new Skin(value, signature);
            }
        }

        throw new SkinNotFoundException();
        
    }

    private static URLConnection setupConnection(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private static UUID fromUntracedUuidString(String input) {
        return UUID.fromString(input.replaceAll(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"));
    }

}
