package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TitleClearCommand extends CustomCommand {
    
    public TitleClearCommand() {
        super("tiraissodaminhatela", CommandRestriction.IN_GAME);
    }
    
    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Title.clear((Player) sender);
        
        Message.SUCCESS.send(sender, "Title limpo!");
    }
}
