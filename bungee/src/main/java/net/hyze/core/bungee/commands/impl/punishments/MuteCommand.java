package net.hyze.core.bungee.commands.impl.punishments;

import net.hyze.core.bungee.punishments.PunishmentHook;

public class MuteCommand extends CustomPunishmentCommand {

    public MuteCommand() {
        super("mute", PunishmentHook.MUTE_TYPE);
    }

}