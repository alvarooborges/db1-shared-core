package net.hyze.core.spigot.commands.impl.tell;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.echo.packets.user.UserTellPacket;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.shared.user.preferences.UserPreference;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.preference.CorePreference;
import org.bukkit.command.CommandSender;

public class ReplyCommand extends CustomCommand {

    public ReplyCommand() {
        super("r", CommandRestriction.IN_GAME);

        registerArgument(new Argument("mensagem", "Mensagem que você deseja enviar.", true));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        UserPreference preferences = CoreProvider.Cache.Local.USERS_PREFERENCES.provide().get(user);

        if (preferences.getPreference(CorePreference.TELL.name()).is(PreferenceStatus.OFF)) {
            Message.ERROR.send(sender, "Você desabilitou suas mensagens privadas! Utilize /preferencias para habilitar esta função novamente.");
            return;
        }

        Integer target = CoreProvider.Cache.Redis.TELL.provide().getTarget(user.getId());

        if (target == null) {
            Message.ERROR.send(sender, "Não há pessoas para responder. :S");
            return;
        }

        User targetUser = CoreProvider.Cache.Local.USERS.provide().get(target);

        if (!targetUser.isLogged()) {
            Message.ERROR.send(sender, String.format("O jogador %s não está online.", targetUser.getNick()));
            return;
        }

        UserPreference targetPreferences = CoreProvider.Cache.Local.USERS_PREFERENCES.provide().get(targetUser);

        if (targetPreferences.getPreference(CorePreference.TELL.name()).is(PreferenceStatus.OFF)) {
            Message.ERROR.send(sender, String.format("O jogador %s está com o recebimento de mensagens privadas desativado.", targetUser.getNick()));
            return;
        }

        CoreProvider.Redis.ECHO.provide().publish(new UserTellPacket(user, targetUser, String.join(" ", args)));
    }

}
