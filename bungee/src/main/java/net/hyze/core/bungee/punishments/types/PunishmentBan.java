package net.hyze.core.bungee.punishments.types;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.punishments.PunishmentType;
import net.hyze.core.shared.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class PunishmentBan extends PunishmentType {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    public PunishmentBan() {
        super("BAN");
    }

    @Override
    public TextComponent getMessage(User user, Punishment punishment) {

        ComponentBuilder componentBuilder = new ComponentBuilder("");

        if (punishment.getLevel().isPermanent() || punishment.getEndedAt() == null) {

            componentBuilder
                    .append("Você está banido permanentemente do servidor.")
                    .color(ChatColor.RED);

        } else {

            componentBuilder
                    .append("Você está banido do servidor até o dia " + DATE_FORMAT.format(punishment.getEndedAt()) + ".")
                    .color(ChatColor.RED);

        }

        User applierUser = CoreProvider.Cache.Local.USERS.provide().get(punishment.getApplierId());

        componentBuilder
                .append("\nMotivo: " + punishment.getInternalDisplayReason())
                .append("\nAutor: " + applierUser.getNick())
//                .append("\n\nUse o ID ")
//                .append("#" + punishment.getId())
//                .color(ChatColor.YELLOW)
//                .append(" para abrir um pedido de revisão em nosso fórum.")
                .color(ChatColor.RED);

        return new TextComponent(componentBuilder.create());

    }

    @Override
    public void apply(Punishment punishment) {
        
        User user = CoreProvider.Cache.Local.USERS.provide().get(punishment.getUserId());
        
        if(user.isLogged() || punishment.getLevel().isPermanent()) {
            punishment.setStartedAt(new Date());
            CoreProvider.Repositories.PUNISHMENTS.provide().updateStartTime(punishment);
        }
        
        if(user.isLogged()) {
            // TODO disconnect banned user
        }
        
        // TODO alert staff members
        
    }
    
}
