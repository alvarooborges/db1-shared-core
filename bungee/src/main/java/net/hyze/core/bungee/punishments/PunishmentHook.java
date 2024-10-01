package net.hyze.core.bungee.punishments;

import net.hyze.core.bungee.punishments.types.PunishmentBan;
import net.hyze.core.bungee.punishments.types.PunishmentMute;
import net.hyze.core.shared.CoreProvider;

public class PunishmentHook {

    public static final PunishmentBan BAN_TYPE = new PunishmentBan();
    public static final PunishmentMute MUTE_TYPE = new PunishmentMute();

    public static void setupPunishmentTypes() {
        
        CoreProvider.Cache.Local.PUNISHMENTS.provide().put(BAN_TYPE);
        CoreProvider.Cache.Local.PUNISHMENTS.provide().put(MUTE_TYPE);

    }

}
