package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.BCrypt;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class ChangePassCommand extends CustomCommand {

    public ChangePassCommand() {
        super("mudarsenha", CommandRestriction.IN_GAME, "trocarsenha");

        registerArgument(new Argument("nova senha", "", true));
        registerArgument(new Argument("nova senha", "", true));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        if (!user.hasGroup(Group.GAME_MASTER) && args.length != 2) {
            Message.ERROR.send(sender, TextComponent.toPlainText(getUsage(sender, getLabel()).create()));
            return;
        }

        User target = user;
        String pass = args[0];
        String confirm = args[1];
        if (args.length == 3 && user.hasGroup(Group.GAME_MASTER)) {
            String targetNick = args[0];
            target = CoreProvider.Repositories.USERS.provide().fetchByNick(targetNick);

            pass = args[1];
            confirm = args[2];
        }

        if (target == null) {
            Message.ERROR.send(sender, "Usuário não encontrado.");
            return;
        }

        if (!pass.equals(confirm)) {
            Message.ERROR.send(sender, "As senhas digitadas estão diferentes.");
            return;
        }

        if (args[1].length() < 5) {
            Message.ERROR.send(sender, "Sua senha deve ter no mínimo 5 letras");
            return;
        }

        if (args[1].length() > 32) {
            Message.ERROR.send(sender, "Sua senha deve ter no máximo 32 letras");
            return;
        }

        if (target.equals(user) && target.hasVerifiedEmail()) {
            Message.EMPTY.send(sender, "&c* Você possui um e-mail confirmado em sua conta.");
            Message.ERROR.send(sender, "&c* Use o fórum para mudar sua senha.");
            return;
        }

        String newPass = BCrypt.hashpw(args[1], BCrypt.gensalt());

        target.setPassword(newPass);

        CoreProvider.Repositories.USERS.provide().update_(target);
        Message.SUCCESS.send(sender, "Senha alterada com sucesso!");
    }

}
