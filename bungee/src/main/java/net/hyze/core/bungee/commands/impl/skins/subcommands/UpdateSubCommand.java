package net.hyze.core.bungee.commands.impl.skins.subcommands;

import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.commands.impl.skins.SkinCommand;
import static net.hyze.core.bungee.commands.impl.skins.SkinCommand.fetchUID;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.misc.mojang.exceptions.SkinNotFoundException;
import net.hyze.core.shared.misc.mojang.exceptions.UUIDNotFoundException;
import net.hyze.core.shared.skins.Skin;
import net.hyze.core.shared.skins.SkinRecord;
import net.hyze.core.shared.skins.SkinRecordType;
import net.hyze.core.shared.user.User;
import java.util.UUID;
import java.util.function.Supplier;
import net.md_5.bungee.api.CommandSender;

public class UpdateSubCommand extends CustomCommand {

    public UpdateSubCommand() {
        super("atualizar");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        UUID uuid = user.getUuid();

        if (uuid == null) {

            Supplier<UUID> uuidSupplier;

            try {
                uuidSupplier = fetchUID(sender, user.getNick());
            } catch (UUIDNotFoundException exception) {
                Message.SUCCESS.send(sender, "Ops! Não conseguimos encontrar este perfil na Mojang. Tente outro nick.");
                return;
            }

            if (uuidSupplier == null) {
                return;
            }

            uuid = uuidSupplier.get();

            user.setUuid(uuid);
            CoreProvider.Repositories.USERS.provide().update(user);

        }

        Supplier<Skin> skinSupplier;

        try {
            skinSupplier = SkinCommand.fetchSkin(sender, uuid);
        } catch (SkinNotFoundException exception) {
            CoreProvider.Repositories.SKINS.provide().clear(user);
            Message.SUCCESS.send(sender, "Ops! Não conseguimos encontrar sua skin na Mojang, por isso foi definida uma skin padrão. Basta relogar.");
            return;
        }

        if (skinSupplier == null) {
            return;
        }

        SkinRecord skinRecord = new SkinRecord(
                user.getNick(),
                skinSupplier.get(),
                SkinRecordType.VANILLA
        );

        CoreProvider.Repositories.SKINS.provide().insertOrUpdate(user, skinRecord);
        Message.SUCCESS.send(sender, "Sucesso! Sua skin foi atualizada. Basta relogar.");

    }

}
