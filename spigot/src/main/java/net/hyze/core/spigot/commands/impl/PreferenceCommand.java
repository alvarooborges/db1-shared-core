package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.preference.PreferenceInventory;
import net.hyze.core.spigot.misc.preference.PreferenceInventoryRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PreferenceCommand extends CustomCommand {

    public PreferenceCommand() {
        super("toggle", CommandRestriction.IN_GAME, "pref", "preferencias");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        if (PreferenceInventoryRegistry.get().isEmpty()) {
            Message.ERROR.send(player, "Ops, este comando está temporáriamente desabilitado.");
            return;
        }
        
        player.openInventory(new PreferenceInventory(user));
    }

}
