package net.hyze.core.spigot.misc.utils;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.packetwrapper.WrapperPlayServerTitle;
import com.google.common.collect.Lists;
import net.hyze.core.shared.user.User;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public class Title {

    private String title = ChatColor.RESET.toString();

    private String subTitle = ChatColor.RESET.toString();

    private int fadeIn;

    private int fadeOut;

    private int stay = 1;

    private boolean translateColorCodes = true;

    public Title title(String title) {
        this.title = translateColorCodes ? ChatColor.translateAlternateColorCodes('&', title) : title;
        return this;
    }

    public Title subTitle(String subTitle) {
        this.subTitle = translateColorCodes ? ChatColor.translateAlternateColorCodes('&', subTitle) : subTitle;
        return this;
    }

    public Title fadeIn(int ticks) {
        this.fadeIn = ticks;
        return this;
    }

    public Title stay(int ticks) {
        this.stay = ticks;
        return this;
    }

    public Title fadeOut(int ticks) {
        this.fadeOut = ticks;
        return this;
    }

    public Title doNotTranslateColorCodes() {
        this.translateColorCodes = false;
        return this;
    }

    public void send(Player... players) {
        List<WrapperPlayServerTitle> packets = Lists.newLinkedList();

        if (fadeIn > 0 || stay > 0 || fadeOut > 0) {
            WrapperPlayServerTitle wrapper = new WrapperPlayServerTitle();
            wrapper.setAction(EnumWrappers.TitleAction.TIMES);
            wrapper.setFadeIn(fadeIn);
            wrapper.setFadeOut(fadeOut);
            wrapper.setStay(stay);
            packets.add(wrapper);
        }

        {
            WrapperPlayServerTitle wrapper = new WrapperPlayServerTitle();
            wrapper.setAction(EnumWrappers.TitleAction.TITLE);
            wrapper.setTitle(WrappedChatComponent.fromText(title));
            packets.add(wrapper);
        }

        {
            WrapperPlayServerTitle wrapper = new WrapperPlayServerTitle();
            wrapper.setAction(EnumWrappers.TitleAction.SUBTITLE);
            wrapper.setTitle(WrappedChatComponent.fromText(subTitle));
            packets.add(wrapper);
        }

        for (Player player : players) {
            for (WrapperPlayServerTitle wrapper : packets) {
                wrapper.sendPacket(player);
            }
        }
    }

    public void send(User... users) {
        Arrays.stream(users).map(user -> Bukkit.getPlayerExact(user.getNick())).filter(Objects::nonNull).forEach(player -> send(player));
    }

    public void send() {
        Bukkit.getOnlinePlayers().stream().forEach((player) -> {
            send(player);
        });
    }

    public static void clear(Player... players) {
        WrapperPlayServerTitle wrapper = new WrapperPlayServerTitle();
        wrapper.setAction(EnumWrappers.TitleAction.CLEAR);

        for (Player player : players) {
            wrapper.sendPacket(player);
        }
    }

}
