package net.hyze.core.bungee.commands.impl.punishments;

import com.google.common.base.Joiner;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.echo.packets.punishments.PunishmentApplyPacket;
import net.hyze.core.shared.echo.packets.user.KickUserPacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.punishments.PunishmentCategory;
import net.hyze.core.shared.punishments.PunishmentLevel;
import net.hyze.core.shared.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PunishCommand extends CustomCommand implements GroupCommandRestrictable {

    private static final Joiner JOINER = Joiner.on("\n");

    public PunishCommand() {
        super("punish", "punir");
    }

    @Override
    public Group getGroup() {
        return Group.HELPER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (args.length == 0) {
            Message.ERROR.send(sender, "Utilize \"/punir <jogador>\" para selecionar um tipo de infração.");
            return;
        }

        String targetName = args[0];
        User targetUser = CoreProvider.Cache.Local.USERS.provide().get(targetName);

        if (targetUser == null) {
            Message.ERROR.send(sender, String.format("O jogador '%s' não existe.", targetName));
            return;
        }

        if (args.length == 1) {

            ComponentBuilder componentBuilder = new ComponentBuilder("\nTipos de punição disponíveis:\n")
                    .color(ChatColor.YELLOW);

            getAvailableCategories(sender, user)
                    .forEach((category) -> {

                        ComponentBuilder hoverComponentBuilder = new ComponentBuilder(category.getDisplayName())
                                .color(ChatColor.YELLOW)
                                .append("\n\n")
                                .color(ChatColor.WHITE);

                        for (String descriptionLine : category.getDescription()) {
                            hoverComponentBuilder
                                    .append(TextComponent.fromLegacyText(descriptionLine))
                                    .append("\n");
                        }

                        hoverComponentBuilder
                                .append("\n")
                                .append("Grupo mínimo: ")
                                .color(ChatColor.WHITE)
                                .append(category.getGroup().getDisplayNameStriped())
                                .color(ChatColor.values()[category.getGroup().getColor().ordinal()])
                                .append("\n\n");

                        AtomicInteger aux = new AtomicInteger(1);

                        category.getLevels().forEach(level -> {
                            hoverComponentBuilder
                                    .append(aux.get() + "º: ")
                                    .color(ChatColor.YELLOW)
                                    .append("[" + level.getType().getName() + "] ")
                                    .color(ChatColor.WHITE);

                            if (level.isPermanent()) {
                                hoverComponentBuilder
                                        .append("Permanente")
                                        .color(ChatColor.RED);
                            } else {
                                hoverComponentBuilder
                                        .append(TimeCode.toText(level.getDuration(), 5));
                            }

                            hoverComponentBuilder.append("\n");
                            aux.addAndGet(1);
                        });

                        componentBuilder
                                .append(category.getDisplayName() + "\n")
                                .color(ChatColor.WHITE)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponentBuilder.create()))
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/punir " + targetUser.getNick() + " " + category.getName()));
                    });

            sender.sendMessage(componentBuilder.create());
            return;

        }

        if (isPlayer(sender)
                && !user.hasGroup(Group.MANAGER)
                && args.length < 3) {
            Message.ERROR.send(sender, "Apenas membros da equipe do grupo " + Group.MANAGER.getDisplayNameStriped() + " ou superior podem punir sem provas.");
            return;
        }

        PunishmentCategory category = CoreProvider.Cache.Local.PUNISHMENTS.provide().getCategory(args[1]);

        if (category == null) {
            Message.ERROR.send(sender, "Tipo de infração inválido, utilize /punir <user> para lista-los.");
            return;
        }

        if (isPlayer(sender) && !user.hasGroup(category.getGroup())) {
            Message.ERROR.send(sender, "Você não tem permissão para efetuar punições desta categoria.");
            return;
        }

        if (!category.isEnabled() && !user.hasGroup(Group.GAME_MASTER)) {
            Message.ERROR.send(sender, "Esta categoria de punição está desativada.");
            return;
        }

        Punishment latestPunishment = CoreProvider.Repositories.PUNISHMENTS.provide().fetchLastestPunishment(targetUser);

        if (latestPunishment != null && latestPunishment.getCreatedAt().after(new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2)))) {
            Message.ERROR.send(sender, String.format("O jogador '%s' foi punido recentemente por este mesmo motivo. Para evitar duplicidade, aguarde alguns minutos para aplicar outra punição.", targetUser.getNick()));
            return;
        }

        String proof = args.length > 2 ? args[2] : null;

        if (proof != null && !(proof.startsWith("http://") || proof.startsWith("https://"))) {
            Message.ERROR.send(sender, "A URL da prova deve iniciar com http:// ou com https://.");
            return;
        }

        Set<Punishment> punishments = CoreProvider.Repositories.PUNISHMENTS.provide().fetchPunishments(user, category);

        int level = punishments.stream().filter((p) -> !p.isRevoked()).mapToInt((l) -> 1).sum();
        PunishmentLevel punishmentLevel = category.getLevelByPunishmentsAmount(level);
//        String hardwareId = CoreProvider.Cache.Redis.USERS_STATUS.provide().getHardwareId(targetUser.getNick());

        Punishment punishment = new Punishment(
                targetUser,
                null,
                user,
                category,
                punishmentLevel,
                null,
                proof
        );

        CoreProvider.Repositories.PUNISHMENTS.provide().insert(punishment);

        punishment.getLevel().getType().apply(punishment);

        if (!punishment.getLevel().getType().getName().equalsIgnoreCase("MUTE")) {
            CoreProvider.Redis.ECHO.provide().publish(new KickUserPacket(targetUser, null));
        }

        Message.INFO.send(sender, "Punição aplicada com sucesso.");

        /**
         *
         */
        CoreProvider.Redis.ECHO.provide().publish(
                new PunishmentApplyPacket(
                        punishment.getUserId(),
                        punishment.getApplierId(),
                        punishment.getCategory().getDisplayName(),
                        proof,
                        punishment.getLevel().getDuration(),
                        punishmentLevel.getType().getName()
                )
        );

    }

    public Stream<PunishmentCategory> getAvailableCategories(CommandSender sender, User user) {

        Map<String, PunishmentCategory> punishmentCategories = CoreProvider.Cache.Local.PUNISHMENTS.provide().getCategories();

        return ((Stream<PunishmentCategory>) (isConsole(sender)
                ? punishmentCategories.values().stream()
                : punishmentCategories.values().stream().filter(category -> {
            return category.isEnabled() && (category.getGroup() == null || user.hasGroup(category.getGroup()));
        })))
                .sorted((category1, category2) -> category1.getDisplayName().compareTo(category2.getDisplayName()));
    }

}
