package net.hyze.core.spigot.commands.impl.tell;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.echo.packets.user.UserTellPacket;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.shared.user.preferences.UserPreference;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.events.chat.PlayerTellChatEvent;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.preference.CorePreference;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TellCommand extends CustomCommand {

    public TellCommand() {
        super("tell", CommandRestriction.IN_GAME);

        registerArgument(new NickArgument("nick", "Nick de quem você deseja enviar a mensagem.", true));
        registerArgument(new Argument("mensagem", "Mensagem que você deseja enviar.", true));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        UserPreference preferences = CoreProvider.Cache.Local.USERS_PREFERENCES.provide().get(user);

        if (preferences.getPreference(CorePreference.TELL.name()).is(PreferenceStatus.OFF)) {
            Message.ERROR.send(sender, "Você desabilitou suas mensagens privadas! Utilize /preferencias para habilitar esta função novamente.");
            return;
        }

        User targetUser = CoreProvider.Cache.Local.USERS.provide().get(args[0]);

        if (targetUser == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não foi encontrado.", args[0]));
            return;
        }

        if (!targetUser.isLogged()) {
            Message.ERROR.send(sender, String.format("O jogador %s não está online.", targetUser.getNick()));
            return;
        }

        UserPreference targetPreferences = CoreProvider.Cache.Local.USERS_PREFERENCES.provide().get(targetUser);

        if (targetPreferences.getPreference(CorePreference.TELL.name()).is(PreferenceStatus.OFF)) {
            Message.ERROR.send(sender, String.format("O jogador %s está com o recebimento de mensagens desativado.", targetUser.getNick()));
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        PlayerTellChatEvent event = new PlayerTellChatEvent((Player) sender, user, targetUser, message);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        CoreProvider.Cache.Redis.TELL.provide().setTarget(user.getId(), targetUser.getId());
        CoreProvider.Cache.Redis.TELL.provide().setTarget(targetUser.getId(), user.getId());

        CoreProvider.Redis.ECHO.provide().publish(new UserTellPacket(user, targetUser, event.getMessage()));
    }
}
