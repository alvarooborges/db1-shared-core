package net.hyze.core.bungee.punishments.types;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.punishments.PunishmentType;
import net.hyze.core.shared.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.md_5.bungee.api.chat.TextComponent;

public class PunishmentMute extends PunishmentType {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    public PunishmentMute() {
        super("MUTE");
    }

    @Override
    public TextComponent getMessage(User user, Punishment punishment) {


        if (punishment.getEndedAt() != null) {
            return new TextComponent("Você está mutado até " + DATE_FORMAT.format(punishment.getEndedAt()));
        }

        return new TextComponent("Você está mutado");
    }

    @Override
    public void apply(Punishment punishment) {

        User user = CoreProvider.Cache.Local.USERS.provide().get(punishment.getUserId());

        if (user.isLogged() || punishment.getLevel().isPermanent()) {
            punishment.setStartedAt(new Date());
            CoreProvider.Repositories.PUNISHMENTS.provide().updateStartTime(punishment);
        }

        if (user.isLogged()) {
            System.out.println("PunishmentMute::apply");
            // TODO alert muted user
        }

        // TODO alert staff members
    }
}
