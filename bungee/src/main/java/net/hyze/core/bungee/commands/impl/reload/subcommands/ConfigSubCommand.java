package net.hyze.core.bungee.commands.impl.reload.subcommands;

import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.packets.config.ConfigReloadPacket;
import net.md_5.bungee.api.CommandSender;

public class ConfigSubCommand extends CustomCommand {

    public ConfigSubCommand() {
        super("config", "c");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        
        CoreProvider.Redis.ECHO.provide().publish(new ConfigReloadPacket(CoreProvider.Repositories.CONFIG.provide().fetch()));
        Message.INFO.send(sender, "Config recarregada em todas as aplicações.");
        
    }

}
