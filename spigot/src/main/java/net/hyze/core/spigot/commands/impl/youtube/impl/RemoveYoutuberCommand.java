package net.hyze.core.spigot.commands.impl.youtube.impl;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.command.CommandSender;

public class RemoveYoutuberCommand extends CustomCommand implements GroupCommandRestrictable {

    public RemoveYoutuberCommand() {
        super("remover", CommandRestriction.IN_GAME);

        registerArgument(new Argument("nick", "Nick do youtuber."));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        User target = CoreProvider.Repositories.USERS.provide().fetchByNick(args[0]);

        if (target == null) {
            Message.ERROR.send(sender, "Usuário não encontrado.");
            return;
        }

        if (CoreProvider.Repositories.YOUTUBERS.provide().isYoutuber(target.getId()) == null) {
            Message.ERROR.send(sender, "Já não possui um canal registrado.");
            return;
        }

        CoreProvider.Repositories.YOUTUBERS.provide().deleteYoutuber(target.getId());

        Message.ERROR.send(sender, "Canal removido com sucesso!");

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
