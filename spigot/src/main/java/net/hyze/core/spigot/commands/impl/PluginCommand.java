package net.hyze.core.spigot.commands.impl;

import com.google.common.collect.Lists;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.updater.JarUpdater;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import java.io.File;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PluginCommand extends CustomCommand {

    public PluginCommand() {
        super("plugin", CommandRestriction.CONSOLE_AND_IN_GAME, "pl");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (user == null || !user.hasGroup(Group.GAME_MASTER)) {
            /*
            * CÓPIA DA SOURCE DO /PLUGINS ORIGINAL DO SPIGOT
             */
            StringBuilder pluginList = new StringBuilder();
            Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

            for (Plugin plugin : plugins) {
                if (pluginList.length() > 0) {
                    pluginList.append(ChatColor.WHITE);
                    pluginList.append(", ");
                }

                pluginList.append(plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
                pluginList.append(plugin.getDescription().getName());
            }

            sender.sendMessage("Plugins (" + plugins.length + "): " + pluginList.toString());
            return;
        }

        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

        ComponentBuilder componentBuilder = new ComponentBuilder("\nPlugins (" + plugins.length + "): ")
                .color(ChatColor.WHITE);

        for (Plugin plugin : plugins) {

            ChatColor color;

            if (!plugin.isEnabled()) {
                color = ChatColor.RED;
            } else {

                JarUpdater jarUpdater = new JarUpdater(new File("plugins/" + plugin.getName() + ".jar"));

                color = jarUpdater.getState().getColor();

            }

            componentBuilder
                    .append(plugin.getName())
                    .color(color)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(MessageUtils.translateColorCodes(
                            Lists.newArrayList(
                                    color + plugin.getName(),
                                    "&fÚltimo commit: &7" + plugin.getDescription().getVersion(),
                                    "",
                                    color + "Clique para copiar!"
                            ).stream().collect(Collectors.joining("\n"))
                    ))))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, plugin.getDescription().getVersion()));

            if (!plugin.equals(plugins[plugins.length - 1])) {
                componentBuilder
                        .append(", ")
                        .color(ChatColor.WHITE);
            }

        }

        componentBuilder
                .append(".\n")
                .color(ChatColor.WHITE);

        ((Player) sender).spigot().sendMessage(componentBuilder.create());
    }

}
