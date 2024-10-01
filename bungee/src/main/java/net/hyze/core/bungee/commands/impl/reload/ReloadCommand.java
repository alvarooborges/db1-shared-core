package net.hyze.core.bungee.commands.impl.reload;

import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.commands.impl.reload.subcommands.ConfigSubCommand;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.group.Group;
import net.md_5.bungee.api.CommandSender;
import net.hyze.core.shared.commands.GroupCommandRestrictable;

public class ReloadCommand extends CustomCommand implements GroupCommandRestrictable {

    public ReloadCommand() {
        super("reload");

        registerSubCommand(new ConfigSubCommand());
    }

    @Override
    public Group getGroup() {
        return Group.MODERATOR;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Message.ERROR.send(sender, "Utilize \"/reload <config>\".");
    }

}
