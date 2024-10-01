package net.hyze.core.bungee.commands.impl.punishments;

import net.hyze.core.bungee.punishments.PunishmentHook;
import net.hyze.core.shared.punishments.PunishmentType;

public class BanCommand extends CustomPunishmentCommand {

    public BanCommand() {
        super("ban", PunishmentHook.BAN_TYPE);
    }

}
