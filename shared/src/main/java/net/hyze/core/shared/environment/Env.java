package net.hyze.core.shared.environment;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Env {

    @Setter
    private static File primaryEnvironmentFile;

    private static final HashMap<String, JsonElement> ENVIRONMENT_MAP = Maps.newHashMap();

    private static JsonObject PRIMARY_ENVIRONMENT_JSON = new JsonObject();
    private static JsonObject SECONDARY_ENVIRONMENT_JSON = new JsonObject();
    private static JsonObject ENVIRONMENT_JSON = new JsonObject();

    private static boolean INITIALIZED = false;

    public static void fetchEnvironment() {

        String envPath = System.getProperty("env");

        if (envPath == null || !(new File(envPath).exists())) {
            envPath = "/home/minecraft/environment.json";
        }

        primaryEnvironmentFile = new File(envPath);

        if (primaryEnvironmentFile.exists()) {
            try {
                String content = FileUtils.readFileToString(primaryEnvironmentFile, Charsets.UTF_8);
                PRIMARY_ENVIRONMENT_JSON = (new Gson()).fromJson(content, JsonObject.class);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        ENVIRONMENT_JSON = mergeJsonObjects(SECONDARY_ENVIRONMENT_JSON, PRIMARY_ENVIRONMENT_JSON);

        mapJsonObject("", ENVIRONMENT_JSON, ENVIRONMENT_MAP);

        Logger.getGlobal().log(Level.INFO, "[Environment] Loaded.");

        INITIALIZED = true;
    }

    public static JsonElement get(String key) {

        if (!INITIALIZED) {
            fetchEnvironment();
        }

        return ENVIRONMENT_MAP.get(key);
    }

    public static String getString(String key, String defaultValue) {
        JsonElement jsonElement = get(key);
        return jsonElement == null ? defaultValue : jsonElement.getAsString();
    }

    public static int getInt(String key, int defaultValue) {
        JsonElement jsonElement = get(key);
        return jsonElement == null ? defaultValue : jsonElement.getAsInt();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        JsonElement jsonElement = get(key);
        return jsonElement == null ? defaultValue : jsonElement.getAsBoolean();
    }

    public static long getLong(String key, long defaultValue) {
        JsonElement jsonElement = get(key);
        return jsonElement == null ? defaultValue : jsonElement.getAsLong();
    }

    public static double getDouble(String key, double defaultValue) {
        JsonElement jsonElement = get(key);
        return jsonElement == null ? defaultValue : jsonElement.getAsDouble();
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static long getLong(String key) {
        return getLong(key, 0L);
    }

    public static double getDouble(String key) {
        return getDouble(key, 0.0D);
    }

    private static void mapJsonObject(String key, JsonObject jsonObject, HashMap<String, JsonElement> map) {

        jsonObject.entrySet().forEach(entry -> {

            StringBuilder builder = new StringBuilder(key);

            if (key.isEmpty()) {
                builder.append(entry.getKey());
            } else {
                builder.append(".").append(entry.getKey());
            }

            if (entry.getValue().isJsonObject()) {
                mapJsonObject(builder.toString(), entry.getValue().getAsJsonObject(), map);
            } else {
                map.put(builder.toString(), entry.getValue());
            }

        });

    }

    private static JsonObject mergeJsonObjects(JsonObject o1, JsonObject o2) {

        JsonObject out = new JsonObject();

        o1.entrySet().stream().forEach((entry) -> {
            out.add(entry.getKey(), entry.getValue());
        });

        o2.entrySet().stream().forEach((entry) -> {
            if (entry.getValue().isJsonObject() && out.has(entry.getKey()) && out.get(entry.getKey()).isJsonObject()) {
                out.add(entry.getKey(), mergeJsonObjects(out.get(entry.getKey()).getAsJsonObject(), entry.getValue().getAsJsonObject()));
            } else {
                out.add(entry.getKey(), entry.getValue());
            }
        });

        return out;
    }
}
