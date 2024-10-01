package net.hyze.core.bungee.commands.impl.punishments;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.Patterns;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.punishments.PunishmentState;
import net.hyze.core.shared.user.User;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CheckPunishmentCommand extends CustomCommand implements GroupCommandRestrictable {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
    private static final ChatColor UNBANNED_COLOR = ChatColor.GRAY;

    public CheckPunishmentCommand() {
        super("checkpunishment", "checkpunish", "checkpunir");
    }

    @Override
    public Group getGroup() {
        return Group.MODERATOR;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (args.length < 1) {
            Message.ERROR.send(sender, "Utilize /checkpunish <nick/ip>.");
            return;
        }

        Set<Punishment> punishments = Sets.newHashSet();
        String targetName = args[0].toLowerCase();
        String[] split = Patterns.COLON.split(targetName);

        if (Patterns.IP.matches(targetName)) {

            punishments = CoreProvider.Repositories.PUNISHMENTS.provide().fetchPunishments(targetName);

        } else if (split.length > 1 && split[0].equals("pid")) {

            Integer pid = Ints.tryParse(split[1]);

            if (pid == null) {
                Message.ERROR.send(sender, "ID da punição inválido, insira um número inteiro positivo.");
                return;
            }

            Punishment punishment = CoreProvider.Repositories.PUNISHMENTS.provide().fetchPunishment(pid);

            if (punishment != null) {
                punishments.add(punishment);
            }

        } else {

            User targetUser = CoreProvider.Cache.Local.USERS.provide().get(targetName);

            if (targetUser == null) {
                Message.ERROR.send(sender, String.format("O jogador '%s' não existe.", targetName));
                return;
            }

            punishments = CoreProvider.Repositories.PUNISHMENTS.provide().fetchPunishments(targetUser);

        }

        if (punishments.isEmpty()) {
            Message.ERROR.send(sender, "Nenhuma punição encontrada.");
            return;
        }

        // checkpunish <jogador>
        ComponentBuilder componentBuilder = new ComponentBuilder("\n")
                .append("█ Pendente")
                .color(PunishmentState.PENDING.getColor())
                .append(" █ Ativo")
                .color(PunishmentState.ACTIVE.getColor())
                .append(" █ Finalizado")
                .color(PunishmentState.ENDED.getColor())
                .append(" █ Desbanido")
                .color(UNBANNED_COLOR)
                .append("\n \n");

        boolean isAdmin = user == null || user.hasGroup(Group.GAME_MASTER);

        ChatColor primaryColor = ChatColor.GOLD;
        ChatColor secondaryColor = ChatColor.GRAY;

        punishments.stream()
                .sorted((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt()))
                .filter((punishment) -> !(punishment.isHidden() && !(user == null || user.hasGroup(Group.GAME_MASTER))))
                .forEach((punishment) -> {

                    User punishedUser = CoreProvider.Repositories.USERS.provide().fetchById(punishment.getUserId());
                    User applierUser = punishment.isAppliedByConsole() ? null : CoreProvider.Repositories.USERS.provide().fetchById(punishment.getApplierId());

                    ComponentBuilder hoverComponentBuilder = new ComponentBuilder("ID: ").color(primaryColor).append(String.valueOf(punishment.getId())).color(secondaryColor)
                            .append("\n")
                            .append("Nick: ").color(primaryColor).append(punishedUser.getNick()).color(secondaryColor)
                            .append("\n")
                            .append("IP: ").color(primaryColor).append(!isAdmin ? "Restrito" : "0.0.0.0").color(secondaryColor)
                            .append("\n")
                            .append("Autor: ").color(primaryColor).append(punishment.isAppliedByConsole() ? "Console" : applierUser.getNick()).color(secondaryColor)
                            .append("\n")
                            .append("Tipo: ").color(primaryColor).append(punishment.getLevel().getType().getName()).color(secondaryColor)
                            .append("\n")
                            .append("Duração: ").color(primaryColor).append(punishment.getLevel().isPermanent() ? "Permanente" : TimeCode.toText(punishment.getLevel().getDuration(), 5)).color(secondaryColor)
                            .append("\n")
                            .append("Data: ").color(primaryColor).append(DATE_FORMAT.format(punishment.getCreatedAt())).color(secondaryColor)
                            .append("\n")
                            .append("Data de início: ").color(primaryColor).append(punishment.getStartedAt() == null ? "Não iniciada" : DATE_FORMAT.format(punishment.getStartedAt())).color(secondaryColor)
                            .append("\n")
                            .append("Data de fim: ").color(primaryColor).append(punishment.getStartedAt() == null ? "Não iniciada" : DATE_FORMAT.format(punishment.getEndedAt())).color(secondaryColor)
                            .append("\n")
                            .append("Categoria: ").color(primaryColor).append(punishment.getCategory() == null ? "Customizada" : punishment.getCategory().getDisplayName()).color(secondaryColor)
                            .append("\n")
                            .append("Motivo: ").color(primaryColor).append(Optional.ofNullable(punishment.getInternalDisplayReason()).orElse("Não informado")).color(secondaryColor)
                            .append("\n")
                            .append("Prova: ").color(primaryColor).append(Optional.ofNullable(punishment.getProof()).orElse("Não informada")).color(secondaryColor);

                    if (punishment.isHidden()) {
                        hoverComponentBuilder
                                .append("Visibilidade: ").color(primaryColor).append("Oculta").color(secondaryColor)
                                .append("\n");
                    }

                    if (punishment.isRevoked()) {

                        User revokerUser = punishment.isRevokedByConsole() ? null : CoreProvider.Repositories.USERS.provide().fetchById(punishment.getRevokerId());

                        hoverComponentBuilder
                                .append("\n\nRevogação: ").color(primaryColor)
                                .append("\n")
                                .append("  Autor: ").color(primaryColor).append(punishment.isRevokedByConsole() ? "Console" : revokerUser.getNick()).color(secondaryColor)
                                .append("\n")
                                .append("  Data: ").color(primaryColor).append(DATE_FORMAT.format(punishment.getRevokedAt())).color(secondaryColor)
                                .append("\n")
                                .append("  Categoria: ").color(primaryColor).append(punishment.getRevokeCategory() == null ? "Customizada" : punishment.getRevokeCategory().getDisplayName()).color(secondaryColor)
                                .append("\n")
                                .append("  Motivo: ").color(primaryColor).append(Optional.ofNullable(punishment.getInternalDisplayRevokeReason()).orElse("Não informado")).color(secondaryColor)
                                .append("\n")
                                .append("  Prova: ").color(primaryColor).append(Optional.ofNullable(punishment.getRevokeProof()).orElse("Não informada")).color(secondaryColor);
                    }

                    componentBuilder
                            .append((punishment.isHidden() ? "[Oculta] " : "") + "[" + DATE_FORMAT.format(punishment.getCreatedAt()) + "] [" + (punishment.getInternalDisplayReason() == null ? "???" : punishment.getInternalDisplayReason()) + "]")
                            .color(punishment.isRevoked() ? UNBANNED_COLOR : ChatColor.values()[punishment.getState().getColor().ordinal()])
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponentBuilder.create()));

                    if (isPlayer(sender)) {

                        if (punishment.getProof() != null) {

                            componentBuilder.append(" [Prova]")
                                    .color(ChatColor.WHITE)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.WHITE + "Clique para copiar: " + ChatColor.GRAY + punishment.getProof())))
                                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, punishment.getProof()));
                        }

                        if (!punishment.isRevoked()) {
                            componentBuilder.append(" [Revogar]")
                                    .color(ChatColor.WHITE)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "Clique para revogar esta punição.")))
                                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/unpunish " + punishment.getId()));

                        }

                    }

                    componentBuilder.append("\n");

                });

        sender.sendMessage(componentBuilder.create());

    }

}
