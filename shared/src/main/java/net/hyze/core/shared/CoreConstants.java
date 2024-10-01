package net.hyze.core.shared;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.InetSocketAddressSerializer;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.gson.Gson;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.regex.Pattern;
import net.hyze.core.shared.misc.jackson.InetSocketAddressDeserializer;
import okhttp3.OkHttpClient;

public class CoreConstants {

    public static final File CLOUD_DIRECTORY = new File("/home/minecraft/cloud");

    public static final Random RANDOM = new Random();
    public static final ObjectMapper JACKSON = new ObjectMapper();
    public static final Gson GSON = new Gson();
    public static final OkHttpClient OKHTTP_CLIENT = new OkHttpClient();

    public static final Pattern URL_PATTERN = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    static {
        JACKSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JACKSON.configure(DeserializationFeature.WRAP_EXCEPTIONS, true);
        JACKSON.registerModule(new GuavaModule());
        JACKSON.setSerializationInclusion(Include.NON_NULL);
        JACKSON.setVisibility(JACKSON.getSerializationConfig().getDefaultVisibilityChecker()
                .with(JsonAutoDetect.Visibility.NONE)
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        SimpleModule module = new SimpleModule();
        module.addSerializer(InetSocketAddress.class, new InetSocketAddressSerializer());
        module.addDeserializer(InetSocketAddress.class, InetSocketAddressDeserializer.instance);

        JACKSON.registerModule(module);
    }

    public static class Infos {

        public static final String NETWORK_NAME = "HYZE";

        public static final String SITE_DOMAIN = "hyze.net";
        public static final String SITE_URL = "https://" + SITE_DOMAIN;

        public static final String STORE_DOMAIN = "loja.hyze.net";
        public static final String STORE_URL = "https://" + STORE_DOMAIN;

        public static final String FORUM_DOMAIN = "forum.hyze.net";
        public static final String FORUM_URL = "https://" + FORUM_DOMAIN;

        public static final String CLIENT_DOMAIN = "client.hyze.net";
        public static final String CLIENT_URL = "https://" + CLIENT_DOMAIN;

        public static final String IP = "hyze.net";

        public static final String DISCORD_INVITE_URL = "https://discord.gg/Ye8RByG";
    }

    public static class Databases {

        public static class Mysql {

            public static class Tables {

                public static final String GROUPS_TABLE_NAME = "groups";
                public static final String APPS_TABLE_NAME = "apps";
                public static final String SERVERS_TABLE_NAME = "servers";
                public static final String CONFIG_TABLE_NAME = "config";

                public static final String USERS_TABLE_NAME = "users";
                public static final String GROUP_USER_TABLE_NAME = "user_groups";
                public static final String PREFERENCE_USER_TABLE_NAME = "user_preferences";
                public static final String PUNISHMENTS_TABLE_NAME = "user_punishments";
                public static final String SKINS = "user_skin";
                public static final String SESSIONS = "user_sessions";

                public static final String PUNISHMENT_CATEGORIES_TABLE_NAME = "punishment_categories";
                public static final String PUNISHMENT_REVOKE_CATEGORIES_TABLE_NAME = "punishment_revoke_categories";

                public static final String DUNGEONS_TABLE_NAME = "dungeons";
                public static final String DUNGEONS_USER_DATA_TABLE_NAME = "dungeons_user_data";
                public static final String DUNGEONS_USER_ACCESSES_TABLE_NAME = "dungeons_user_accesses";

                public static final String YOUTUBERS_TABLE_NAME = "youtubers";
                public static final String YOUTUBERS_VIDEOS_TABLE_NAME = "youtubers_videos";

            }
        }
    }

    public static class Redis {

        public static class Main {

            public static final String STATUS_FIELD = "statuses";
            public static final String USERS_FIELD = "users";
        }
    }
}
