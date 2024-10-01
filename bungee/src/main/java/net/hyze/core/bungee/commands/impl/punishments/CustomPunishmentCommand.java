package net.hyze.core.bungee.commands.impl.punishments;

import java.util.Arrays;
import java.util.stream.Collectors;
import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.echo.packets.user.KickUserPacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.punishments.PunishmentLevel;
import net.hyze.core.shared.punishments.PunishmentType;
import net.hyze.core.shared.user.User;
import net.md_5.bungee.api.CommandSender;
import org.apache.commons.validator.routines.UrlValidator;

public abstract class CustomPunishmentCommand extends CustomCommand implements GroupCommandRestrictable {

    private final PunishmentType type;

    public CustomPunishmentCommand(String name, PunishmentType type, String... aliases) {
        super(name, aliases);
        this.type = type;
    }

    @Override
    public Group getGroup() {
        return Group.MANAGER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (args.length < 2) {
            Message.ERROR.send(sender, String.format("Utilize \"/%s <user> <duração> [motivo]\".", getName()));
            return;
        }

        String targetName = args[0];
        User targetUser = CoreProvider.Cache.Local.USERS.provide().get(targetName);

        if (targetUser == null) {
            Message.ERROR.send(sender, String.format("O jogador '%s' não existe.", targetName));
            return;
        }

        String proof = null;

        if (new UrlValidator().isValid(args[args.length - 1])) {
            proof = args[args.length - 1];
        }

        long duration = TimeCode.parse(args[1]);
        String reason = args.length < 3 ? null : Arrays.stream(args)
                .skip(2)
                .limit(proof == null ? args.length - 2 : args.length - 3)
                .collect(Collectors.joining(" "));

        String hardwareId = CoreProvider.Cache.Redis.USERS_STATUS.provide().getHardwareId(targetUser.getNick());

        Punishment punishment = new Punishment(
                targetUser,
                hardwareId,
                user,
                null,
                new PunishmentLevel(duration == 0 ? null : duration, type),
                reason,
                proof
        );

        CoreProvider.Repositories.PUNISHMENTS.provide().insert(punishment);
        punishment.getLevel().getType().apply(punishment);

        CoreProvider.Redis.ECHO.provide().publish(new KickUserPacket(targetUser, null));

        Message.INFO.send(sender, "Punição customizada aplicada com sucesso.");

    }

}
