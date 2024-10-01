package net.hyze.core.spigot.commands.impl;

import com.google.common.base.Predicates;
import com.google.common.primitives.Ints;
import net.hyze.core.shared.commands.CommandRestrictable;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand extends CustomCommand implements CommandRestrictable {

    public GameModeCommand() {
        super("gamemode", CommandRestriction.IN_GAME, "gm");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;

        if (args.length < 1) {
            Message.ERROR.send(sender, "Utilize: &7/gm <id> [player].");
            return;
        }

        Integer id = Ints.tryParse(args[0]);

        if (id == null) {
            Message.ERROR.send(sender, "Modo de jogo inválido.");
            return;
        }

        GameMode gameMode = GameMode.getByValue(id);

        if (gameMode == null) {
            Message.ERROR.send(sender, "Modo de jogo inválido.");
            return;
        }

        if (!CoreSpigotConstants.ALLOW_GAMEMODE.getOrDefault(gameMode, Predicates.alwaysTrue()).apply(user)) {
            Message.ERROR.send(sender, String.format(
                    "Péh, você não pode utilizar o gamemode %s!",
                    gameMode.name()
            ));
            return;
        }

        if (!(user.hasGroup(Group.GAME_MASTER) || player.isOp()) && gameMode == GameMode.CREATIVE) {
            Message.ERROR.send(sender, "Péh, você não pode utilizar o gamemode CREATIVE!");
            return;
        }

        Player targetPlayer = null;

        if (args.length > 1 && (user.hasGroup(Group.GAME_MASTER) || player.isOp())) {

            targetPlayer = Bukkit.getPlayerExact(args[1]);

            if (targetPlayer == null) {
                Message.ERROR.send(sender, String.format("O jogador \"%s\" não está online.", args[1]));
                return;
            }

        }

        if (targetPlayer != null && player != targetPlayer) {
            String string = String.format("Game Mode de \"%s\" alterado para &f%s&a.", targetPlayer.getName(), gameMode.name());
            Message.SUCCESS.send(sender, string);

            targetPlayer.setGameMode(gameMode);
            return;
        }

        player.setGameMode(gameMode);
        Message.SUCCESS.send(sender, "Game Mode alterado para &f" + gameMode.name() + "&a.");

    }

    @Override
    public boolean canExecute(User user) {
        return user.hasGroup(Group.MODERATOR) || user.hasStrictGroup(Group.BUILDER);
    }

    @Override
    public String getErrorMessage() {
        return "Ops, voce nao tem permissao para utilizar este comando.";
    }

}
