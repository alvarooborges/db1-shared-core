package net.hyze.core.bungee.commands.impl.punishments;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.punishments.PunishmentConstants;
import net.hyze.core.shared.punishments.PunishmentRevokeCategory;
import net.hyze.core.shared.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.validator.routines.UrlValidator;

public class RevokeCommand extends CustomCommand implements GroupCommandRestrictable {

    private static final Joiner JOINER = Joiner.on("\n");

    public RevokeCommand() {
        super("revoke", "unpunish");
    }

    @Override
    public Group getGroup() {
        return Group.MANAGER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (args.length == 0) {
            Message.ERROR.send(sender, "Utilize \"/revoke <ID da punição> <category/reason> [proof]\".");
            return;
        }

        Integer punishmentId = Ints.tryParse(args[0].replace("#", ""));

        if (punishmentId == null) {
            Message.ERROR.send(sender, "O ID informado é inválido.");
            return;
        }

        Punishment punishment = CoreProvider.Repositories.PUNISHMENTS.provide().fetchPunishment(punishmentId);

        if (punishment == null) {
            Message.ERROR.send(sender, String.format("Não existe uma punição associada ao ID \"%d\".", punishmentId));
            return;
        }

        if (args.length == 1) {
            ComponentBuilder componentBuilder = new ComponentBuilder("\nCategorias de revogação de punição disponíveis:\n")
                    .color(ChatColor.YELLOW);

            getAvailableCategories(sender, user)
                    .forEach((category) -> {

                        componentBuilder
                                .append(category.getDisplayName() + "\n")
                                .color(ChatColor.WHITE)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(JOINER.join(category.getFullDescription()))))
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/revoke %d %s", punishmentId, category.getName())));

                    });

            sender.sendMessage(componentBuilder.create());
            return;
        }

        if (args.length >= 2) {

            PunishmentRevokeCategory revokeCategory = CoreProvider.Cache.Local.PUNISHMENTS.provide().getRevokeCategory(args[1]);
            String reason = null;
            String proof = null;

            if (revokeCategory != null) {

                if (revokeCategory.isApprovedAppealRevokeCategory()) {

                    if (args.length != 3) {
                        Message.ERROR.send(sender, String.format("Você deve informar o link da revisão de punição. Utilize \"/revoke %d %s <link>\".", punishmentId, PunishmentConstants.APPROVED_APPEAL_REVOKE_CATEGORY));
                        return;
                    }

                    proof = args[2];

                    if (!new UrlValidator().isValid(proof)) {
                        Message.ERROR.send(sender, "Informe uma URL válida.");
                        return;
                    }

                }

            } else {

                if (isPlayer(sender) && !user.hasGroup(Group.MODERATOR)) {
                    Message.ERROR.send(sender, "Apenas membros da equipe do grupo " + Group.MODERATOR.getDisplayNameStriped() + " ou superior podem remover uma punição com motivo customizado.");
                    return;
                }

                if (new UrlValidator().isValid(args[args.length - 1])) {
                    proof = args[args.length - 1];
                }

                reason = Arrays.stream(args)
                        .skip(1)
                        .limit(proof == null ? args.length - 1 : args.length - 2)
                        .collect(Collectors.joining(" "));

            }

            if (punishment.isRevoked()) {
                Message.ERROR.send(sender, "Esta punição já foi revogada.");
                return;
            }

            if (isPlayer(sender) && !user.hasGroup(Group.MODERATOR)) {

                Date currentDate = new Date();

                if (currentDate.after(new Date(punishment.getCreatedAt().getTime() + PunishmentConstants.MANAGER_ONLY_REVOKE_RESTRICTION_OFFSET))) {
                    Message.ERROR.send(sender, "Essa punição foi aplicada há mais de 12 dias e por isso só poderá ser revogada por um " + Group.MODERATOR.getDisplayNameStriped() + " ou um superior.");
                    return;
                }

                if (!user.hasGroup(Group.MODERATOR)) {

                    if (currentDate.after(new Date(punishment.getCreatedAt().getTime() + PunishmentConstants.ADMIN_ONLY_REVOKE_RESTRICTION_OFFSET))) {
                        Message.ERROR.send(sender, "Essa punição foi aplicada há mais de 3 horas e por isso só poderá ser revogada por um " + Group.MODERATOR.getDisplayNameStriped() + " ou um superior.");
                        return;
                    }

                    if (!punishment.getApplierId().equals(user.getId())) {
                        User applierUser = CoreProvider.Cache.Local.USERS.provide().get(punishment.getApplierId());
                        Message.ERROR.send(sender, "Apenas o staffer " + applierUser.getNick() + ", um administrador ou um superior podem remover essa punição.");
                        return;
                    }
                }
            }

            punishment.setRevokerId(isConsole(sender) ? 0 : user.getId());
            punishment.setRevokedAt(new Date());
            punishment.setRevokeCategory(revokeCategory);
            punishment.setRevokeReason(reason);
            punishment.setRevokeProof(proof);
            
            CoreProvider.Repositories.PUNISHMENTS.provide().updateRevoke(punishment);

            Message.INFO.send(sender, String.format("Punição &b#%d&e revogada com sucesso.", punishmentId));
            
        }

    }

    public Stream<PunishmentRevokeCategory> getAvailableCategories(CommandSender sender, User user) {

        Map<String, PunishmentRevokeCategory> punishmentRevokeCategories = CoreProvider.Cache.Local.PUNISHMENTS.provide().getRevokeCategories();

        return ((Stream<PunishmentRevokeCategory>) (isConsole(sender)
                ? punishmentRevokeCategories.values().stream()
                : punishmentRevokeCategories.values().stream().filter(category -> {
                    return category.isEnabled() && (category.getGroup() == null || user.hasGroup(category.getGroup()));
                })))
                .sorted((category1, category2) -> category1.getDisplayName().compareTo(category2.getDisplayName()));
    }

}
