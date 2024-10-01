package net.hyze.core.bungee.commands.impl.skins;

import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.commands.impl.skins.subcommands.UpdateSubCommand;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.mojang.MojangAPI;
import net.hyze.core.shared.misc.mojang.exceptions.SkinNotFoundException;
import net.hyze.core.shared.misc.mojang.exceptions.TooManyRequestsException;
import net.hyze.core.shared.misc.mojang.exceptions.UUIDNotFoundException;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.skins.Skin;
import net.hyze.core.shared.skins.SkinRecord;
import net.hyze.core.shared.skins.SkinRecordType;
import net.hyze.core.shared.user.User;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;
import net.md_5.bungee.api.CommandSender;

public class SkinCommand extends CustomCommand {

    public SkinCommand() {
        super("skin", CommandRestriction.IN_GAME);

        registerSubCommand(new UpdateSubCommand());
        registerArgument(new NickArgument("nick", "Nick da skin desejada.", false));
    }

    public static Supplier<UUID> fetchUID(CommandSender sender, String nick) throws UUIDNotFoundException {

        try {
            UUID uid = MojangAPI.getUUID(nick);
            return () -> uid;
        } catch (IOException exception) {
            Message.ERROR.send(sender, "Ops! Parece que houve um problema ao buscar seu perfil na Mojang. Tente novamente mais tarde. (1)");
            return null;
        } catch (TooManyRequestsException exception) {
            Message.ERROR.send(sender, "Ops! Parece que houve um problema ao buscar seu perfil na Mojang. Tente novamente mais tarde. (2)");
            return null;
        }

    }

    public static Supplier<Skin> fetchSkin(CommandSender sender, UUID uuid) throws SkinNotFoundException {

        try {
            Skin skin = MojangAPI.getSkin(uuid);
            return () -> skin;
        } catch (IOException exception) {
            Message.ERROR.send(sender, "Ops! Parece que houve um problema ao buscar sua skin na Mojang. Tente novamente mais tarde. (1)");
            return null;
        } catch (TooManyRequestsException exception) {
            Message.ERROR.send(sender, "Ops! Parece que houve um problema ao buscar sua skin na Mojang. Tente novamente mais tarde. (2)");
            return null;
        }

    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (args.length == 0) {
            Message.ERROR.send(sender, " Utilize \"/skin atualizar\" para atualizar sua skin ou \"/skin <nick>\" para definir uma skin customizada.");
            return;
        }

        if (args.length > 0) {

            if (!user.hasGroup(Group.VIP)) {
                Message.ERROR.send(sender, "Apenas jogadores VIP podem definir uma skin customizada.");
                return;
            }

            String targetNick = args[0];
            User targetUser = CoreProvider.Repositories.USERS.provide().fetchByNick(targetNick);
            UUID uuid;

            if (targetUser != null && targetUser.getUuid() != null) {
                uuid = targetUser.getUuid();
            } else {

                Supplier<UUID> uuidSupplier;

                try {
                    uuidSupplier = fetchUID(sender, targetNick);
                } catch (UUIDNotFoundException exception) {
                    Message.SUCCESS.send(sender, "Ops! Não conseguimos encontrar este perfil na Mojang. Tente outro nick.");
                    return;
                }

                if (uuidSupplier == null) {
                    return;
                }

                uuid = uuidSupplier.get();

            }

            Supplier<Skin> skinSupplier;

            try {
                skinSupplier = fetchSkin(sender, uuid);
            } catch (SkinNotFoundException exception) {
                Message.SUCCESS.send(sender, "Ops! Não conseguimos encontrar esta skin na Mojang. Tente outro nick.");
                return;
            }

            if (skinSupplier == null) {
                return;
            }

            SkinRecord skinRecord = new SkinRecord(
                    targetNick,
                    skinSupplier.get(),
                    SkinRecordType.CUSTOM
            );

            CoreProvider.Repositories.SKINS.provide().insertOrUpdate(user, skinRecord);
            Message.SUCCESS.send(sender, "Sucesso! Sua skin foi atualizada. Basta relogar.");

        }

    }

}
