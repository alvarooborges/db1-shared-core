package net.hyze.core.spigot.commands.impl.basics;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.shared.user.preferences.UserPreference;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.echo.packets.UserPreferenceUpdatePacket;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    private Group group = Group.MODERATOR;

    public static final String PREFERENCE_KEY = "GOD_MODE";

    public GodCommand() {
        super("god", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        UserPreference preferences = CoreProvider.Cache.Local.USERS_PREFERENCES.provide().get(user);

        PreferenceStatus status = preferences.getPreference(PREFERENCE_KEY, PreferenceStatus.OFF);

        preferences.setPreference(PREFERENCE_KEY, status.opposite());

        CoreProvider.Repositories.USERS_PREFERENCES.provide().updateUserPreference(PREFERENCE_KEY, status.opposite(), user);
        CoreProvider.Redis.ECHO.provide().publish(new UserPreferenceUpdatePacket(user));

        if (status.is(PreferenceStatus.ON)) {
            Message.ERROR.send(sender, "Você desativou o modo Deus.");
        } else {
            Message.SUCCESS.send(sender, "Você ativou o modo Deus.");
        }
    }

    public static boolean anyGod(Player... players) {
        boolean anyGod = false;


        for (Player player : players) {
            if (player != null) {
                User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());
                UserPreference preferences = CoreProvider.Cache.Local.USERS_PREFERENCES.provide().get(user);

                anyGod |= preferences.getPreference(GodCommand.PREFERENCE_KEY, PreferenceStatus.OFF)
                        .is(PreferenceStatus.ON);
            }

            if (anyGod) {
                break;
            }
        }

        return anyGod;
    }
}
