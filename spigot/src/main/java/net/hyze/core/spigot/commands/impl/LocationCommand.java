package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import java.text.DecimalFormat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.hyze.core.shared.commands.GroupCommandRestrictable;

public class LocationCommand extends CustomCommand implements GroupCommandRestrictable {

    public LocationCommand() {
        super("location", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;

        Location l = player.getLocation();
        DecimalFormat format = new DecimalFormat("#.##");

        /**
         * Copiei do sky, não me bata.
         */
        Message.INFO.send(sender, "Sua localização atual: \n" + "&6  WORLD: &e" + l.getWorld().getName() + "\n  &6X: &e" + format.format(l.getX())
                + "\n  &6Y: &e" + format.format(l.getBlockY()) + "\n  &6Z: &e" + format.format(l.getZ()) + "\n  &6YAW: &e" + l.getYaw() + "\n  &6PITCH: &e" + l.getPitch());

        TextComponent text = new TextComponent("Clique para copiar");
        text.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, TextComponent.fromLegacyText("&7Clique para copiar")));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "new Location(Bukkit.getWorld(\"" + l.getWorld().getName() + "\"), " + format.format(l.getX()) + ", "
                + format.format(l.getY()) + ", " + format.format(l.getZ()) + ", " + format.format(l.getYaw()) + "F, " + format.format(l.getPitch()) + "F)"));

        player.spigot().sendMessage(text);

    }

    @Override
    public Group getGroup() {
        return Group.HELPER;
    }
}
