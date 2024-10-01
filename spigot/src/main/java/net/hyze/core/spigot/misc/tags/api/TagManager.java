package net.hyze.core.spigot.misc.tags.api;

import net.hyze.core.shared.misc.utils.Printer;
import org.bukkit.entity.Player;
import net.hyze.core.spigot.misc.tags.utils.TagDataMap;

import java.util.Collection;
import java.util.Map;

public class TagManager {

    private final static Object LOCK = new Object();
    private final static TagDataMap datas = new TagDataMap();

    public static void setTag(Player player, String prefix) {
        setTag(player, prefix, "");
    }

    public static void setTag(Player player, String prefix, String suffix) {
        setTag(player, player.getName(), prefix, suffix);
    }

    public static void setTag(Player player, String team, String prefix, String suffix) {
        setTag(player, team, TagVisibility.ALWAYS, prefix, suffix);
    }

    public static void setTag(Player player, String team, TagVisibility tagVisibility, String prefix, String suffix) {
        team = team.toLowerCase();

        synchronized (LOCK) {
            if (hasTag(player)) {
                clearTag(player);
            }

            TagData data = datas.get(team);
            
            if (data == null) {
                data = new TagData(team, tagVisibility, prefix, suffix);
                datas.put(team, data);

                data.getPacket().send();

                if (data.getName().contains(player.getName())) {
                    data.destroy();
                    data.getPacket().send();
                }

            }

            data.addPlayer(player);
        }
    }

    public static void sendTags(Player player) {
        datas.values().forEach(data -> data.getPacket().send(player));
    }

    public static void clearTags() {
        datas.values().forEach(TagManager::removeTeam);
    }

    public static void removeTeam(TagData team) {
        removeTeam(team.getName());
    }

    public static void removeTeam(String team) {
        if (datas.get(team) == null) {
            return;
        }
        TagData data = datas.get(team);
        data.destroy();
        datas.remove(team);
    }

    public static void clearTag(Player player) {
        datas.values()
                .stream()
                .filter(data -> {
                    return data.hasPlayer(player);
                })
                .forEach(data -> {
                    data.removePlayer(player);
                });
    }

    public static boolean hasTag(Player player) {
        return getTagData(player) != null;
    }

    public static TagData getTagData(Player player) {
        for (TagData data : datas.values()) {
            if (!data.hasPlayer(player)) {
                continue;
            }
            return data;
        }
        return null;
    }

    public static Map<String, TagData> getDatasMap() {
        return datas;
    }

    public static Collection<TagData> getDatas() {
        return datas.values();
    }
}
