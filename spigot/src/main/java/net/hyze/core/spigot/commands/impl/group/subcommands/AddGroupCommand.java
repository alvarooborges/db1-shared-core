package net.hyze.core.spigot.commands.impl.group.subcommands;

import com.google.common.base.Enums;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.echo.packets.group.GroupUpdateAction;
import net.hyze.core.shared.echo.packets.group.GroupUpdatePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.command.CommandSender;

public class AddGroupCommand extends CustomCommand {

    public AddGroupCommand() {
        super("add");

        registerArgument(new Argument("nick", "Nick de quem você deseja alterar o grupo.", true));
        registerArgument(new Argument("grupo", "Grupo que você deseja definir.", true));
//        registerArgument(new Argument("serverId", "Id do servidor.", false));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        String nickRaw = args[0];
        String groupRaw = args[1].toUpperCase();
//        String serverIdRaw;

//        if (args.length >= 3) {
//            serverIdRaw = args[2];
//        } else {
//            if (CoreProvider.getApp().getServer() == null) {
//                Message.ERROR.send(sender, "Servidor não definido.");
//                return;
//            }
//
//            serverIdRaw = CoreProvider.getApp().getServer().getId();
//        }

        User target = CoreProvider.Cache.Local.USERS.provide().get(nickRaw);

        if (target == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não foi encontrado.", nickRaw));
            return;
        }

//        Server server = Server.getById(serverIdRaw).orNull();
//
//        if (server == null) {
//            Message.ERROR.send(sender, String.format("O servidor %s não existe.", serverIdRaw));
//            return;
//        }

        Group group = Enums.getIfPresent(Group.class, groupRaw).orNull();

        if (group == null) {
            Message.ERROR.send(sender, String.format("O grupo %s não existe.", groupRaw));
            return;
        }

        if (target.hasStrictGroup(group)) {
            Message.ERROR.send(sender, String.format("O jogador já possui o grupo %s.", groupRaw));
            return;
        }

        if (group.isSameOrHigher(user.getHighestGroup())) {
            Message.ERROR.send(sender, "Você não pode adicionar um grupo igual ou superior ao seu.");
            return;
        }

        CoreProvider.Repositories.GROUPS.provide().addGroup(target, group);

        Message.SUCCESS.send(sender, "Grupo definido com sucesso!");

        CoreProvider.Redis.ECHO.provide().publish(new GroupUpdatePacket(user, target, group, GroupUpdateAction.ADD));

    }
}
