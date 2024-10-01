package net.hyze.core.bungee.commands.impl;

import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.md_5.bungee.api.CommandSender;

public class HelpCommand extends CustomCommand {

    public HelpCommand() {
        super("ajuda", CommandRestriction.IN_GAME, "help");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Message.SUCCESS.send(sender, "\n Precisando de ajuda?\n A maneira mais rápida de resolver qualquer problema é atráves de nosso fórum!\n Acesse agora: &fhttps://forum.hyze.net/ \n ");
    }
}
